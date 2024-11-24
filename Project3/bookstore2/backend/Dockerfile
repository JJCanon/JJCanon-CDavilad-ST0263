# Usar Alpine como base por ser más ligera
FROM node:18-alpine

# Instalar curl para el healthcheck
RUN apk add --no-cache curl

# Crear directorio de la aplicación
WORKDIR /usr/src/app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias usando ci para una instalación más limpia y reproducible
RUN npm ci --only=production

# Copiar el código fuente
COPY . .

# Exponer el puerto que usa la aplicación
EXPOSE 5001

# Comando para iniciar la aplicación
CMD ["npm", "start"]