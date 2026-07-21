# ==============================================================================
# Dockerfile Multi-Stage Build para Frontend React + Nginx
# ==============================================================================
# Stage 1: Compilación de Node y empaquetado optimizado con Vite
FROM node:20-alpine as build
WORKDIR /app

# Copia e instalación de dependencias
COPY package.json package-lock.json* ./
RUN npm install

# Copia de archivos fuente y construcción del bundle estático
COPY . .
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# Stage 2: Servidor Ligero Nginx
FROM nginx:alpine as production

# Reemplazamos la configuración por defecto por nuestro nginx.conf optimizado
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiamos la carpeta 'dist' generada en la primera etapa
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
