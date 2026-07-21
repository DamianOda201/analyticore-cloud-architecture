# ==============================================================================
# Dockerfile Multi-Stage Build optimizado para Producción - Worker Java
# ==============================================================================
# Patrón Cloud: Se usa una primera etapa con Maven para compilar el código sin
# incluir todo el código fuente en la imagen final, y una segunda etapa (JRE)
# muy ligera para ejecutar el JAR en producción o en Render.

# Etapa 1: Compilación (Build Stage)
FROM maven:3.9.6-eclipse-temurin-21 as build
WORKDIR /app

# Copiamos primero el archivo pom.xml y descargamos dependencias para optimizar la caché de capas
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente e iniciamos el empaquetado del JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución Ligera (Run Stage)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creamos usuario no privilegiado para mayor seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiamos únicamente el archivo JAR generado desde la etapa de compilación
COPY --from=build /app/target/java-service-1.0.0.jar app.jar

# Exponemos el puerto 8081 para el worker
EXPOSE 8081

# Ejecutamos el microservicio con optimizaciones de JVM para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
