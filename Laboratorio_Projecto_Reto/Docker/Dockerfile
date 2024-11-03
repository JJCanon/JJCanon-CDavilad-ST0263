FROM wordpress:php8.1-apache

# Evitar interacción durante la instalación de paquetes
ENV DEBIAN_FRONTEND=noninteractive

# Instalar extensiones PHP y dependencias necesarias
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

# Habilitar mod_rewrite
RUN a2enmod rewrite

# Copiar configuración personalizada de PHP
COPY php.ini /usr/local/etc/php/conf.d/php.ini

# Copiar archivo de salud
COPY health.html /var/www/html/health.html

# Configurar permisos
RUN chown -R www-data:www-data /var/www/html && \
    find /var/www/html/ -type d -exec chmod 750 {} \; && \
    find /var/www/html/ -type f -exec chmod 640 {} \;