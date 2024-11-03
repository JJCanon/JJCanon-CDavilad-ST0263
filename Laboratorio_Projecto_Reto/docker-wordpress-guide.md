# Configuración de Docker en i-WebServer para WordPress
![AWS WordPress Setup](https://via.placeholder.com/800x400?text=AWS+WordPress+Setup)

## 📋 Tabla de Contenidos
- [Descripción](#descripción)
- [Requisitos Previos](#requisitos-previos)
- [Pasos de Configuración](#pasos-de-configuración)
- [Configuración de SSL y Dominio](#configuración-de-ssl-y-dominio)
- [Configuración del Balanceador de Carga](#configuración-del-balanceador-de-carga)

## 📝 Descripción
Este documento proporciona una guía detallada para configurar Docker y Docker Compose en una instancia `i-WebServer` de AWS como parte de un laboratorio para desplegar WordPress con Elastic Load Balancing (ELB) y Auto Scaling.

## ⚡ Requisitos Previos
1. Completar los pasos del laboratorio "PDF-Laboratorio-Wordpress-ELB-AutoScaling" hasta el punto 7.3
2. Acceso a una instancia `i-WebServer` a través de un `BastionHost`
3. Credenciales de conexión (archivo `.pem` o configuraciones SSH)

## 🚀 Pasos de Configuración

### 1. Conexión a la Instancia
```bash
# Conexión al BastionHost
ssh -i "ruta_a_tu_archivo.pem" usuario@IP_BastionHost

# Conexión a i-WebServer desde BastionHost
ssh usuario@IP_i-WebServer
```

### 2. Instalación de Docker
```bash
# Actualizar repositorios
sudo apt update

# Instalar dependencias
sudo apt install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common

# Agregar clave GPG de Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
    sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Configurar repositorio Docker
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
    https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker y Docker Compose
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose

# Configurar permisos
sudo usermod -aG docker $USER

# Verificar instalación
docker --version
docker-compose --version
```

### 3. Preparación del Proyecto
```bash
mkdir wordpress-docker
cd wordpress-docker
```

### 4. Creación de Archivos de Configuración

#### 📄 docker-compose.yml
```yaml
version: '3'

services:
  wordpress:
    build: .
    ports:
      - "80:80"
    volumes:
      - ./wordpress:/var/www/html
      - ./php.ini:/usr/local/etc/php/conf.d/php.ini
    environment:
      WORDPRESS_DB_HOST: 172.16.3.196
      WORDPRESS_DB_NAME: wordpress
      WORDPRESS_DB_USER: wpuser
      WORDPRESS_DB_PASSWORD: wppassword
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/health.html"]
      interval: 30s
      timeout: 10s
      retries: 3
```

#### 📄 Dockerfile
```dockerfile
FROM wordpress:php8.1-apache

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    libfreetype6-dev \
    libjpeg62-turbo-dev \
    libpng-dev \
    libzip-dev \
    libxml2-dev \
    libssl-dev \
    libicu-dev \
    && rm -rf /var/lib/apt/lists/* \
    && docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) \
        gd \
        mysqli \
        zip \
        soap \
        intl

RUN a2enmod rewrite

COPY php.ini /usr/local/etc/php/conf.d/php.ini
COPY health.html /var/www/html/health.html

RUN chown -R www-data:www-data /var/www/html && \
    find /var/www/html/ -type d -exec chmod 750 {} \; && \
    find /var/www/html/ -type f -exec chmod 640 {} \;
```

#### 📄 php.ini
```ini
upload_max_filesize = 64M
post_max_size = 64M
max_execution_time = 300
max_input_time = 300
memory_limit = 256M
```

### 5. Configuración de WordPress

```bash
# Crear archivo de salud
touch health.html

# Descargar WordPress
curl -O https://wordpress.org/latest.tar.gz
tar xzvf latest.tar.gz
mv wordpress html

# Configurar wp-config.php
cp html/wp-config-sample.php html/wp-config.php

# Modificar configuración
sed -i 's/database_name_here/wordpress/' html/wp-config.php
sed -i 's/username_here/wpuser/' html/wp-config.php
sed -i 's/password_here/wppassword/' html/wp-config.php
sed -i 's/localhost/172.16.3.196/' html/wp-config.php
```

### 6. Iniciar Contenedores
```bash
docker-compose up -d --build
docker ps
curl localhost/health.html
```

## 🔒 Configuración de SSL y Dominio

### Configuración HTTPS en WordPress
```php
/* Configuración SSL */
define('FORCE_SSL_ADMIN', true);
if (strpos($_SERVER['HTTP_X_FORWARDED_PROTO'], 'https') !== false)
    $_SERVER['HTTPS']='on';

/* Define el dominio del sitio */
define('WP_HOME','https://www.tu-dominio.com');
define('WP_SITEURL','https://www.tu-dominio.com');

/* Forzar HTTPS */
define('FORCE_SSL_ADMIN', true);
define('FORCE_SSL_LOGIN', true);
```

### Configuración de Dominio y SSL/TLS
1. **Route 53**:
   - Crear zona alojada
   - Configurar registros NS en el proveedor de dominio
   
2. **Certificate Manager**:
   - Solicitar certificado
   - Validar dominio
   - Crear registros DNS

## 🔄 Configuración del Balanceador de Carga

### Crear Application Load Balancer
1. **Configuración Básica**:
   - Nombre: `lb-WebCMS`
   - Esquema: Internet-facing

2. **Network Mappings**:
   - VPC: CMS-vpc
   - AZ1: us-east-1a (CMS-subnet-public1)
   - AZ2: us-east-1b (CMS-subnet-public2)

3. **Seguridad**:
   - Crear nuevo Security Group (SG-LB)
   - Permitir HTTP (80) y HTTPS (443)

4. **Listeners**:
   - HTTPS: Puerto 443
   - HTTP: Puerto 80 (redirección a HTTPS)
   - Certificado SSL/TLS de ACM

### Configuración DNS Final
- Crear registro tipo A en Route 53
- Alias hacia el balanceador de carga
- Verificar la propagación DNS

## 📌 Notas Finales
- Continuar con el paso 9 del laboratorio original
- Verificar el acceso al sitio mediante el dominio configurado
- Comprobar el funcionamiento del SSL/TLS
