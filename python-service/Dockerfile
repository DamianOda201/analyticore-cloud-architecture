# ==============================================================================
# Dockerfile optimizado para Producción - Servicio de Submisión (Python/FastAPI)
# ==============================================================================
# Se utiliza una imagen base ligera (slim) para minimizar el tamaño final,
# cumpliendo con los patrones cloud 12-Factor y buenas prácticas de seguridad.

FROM python:3.11-slim as builder

# Variable de entorno para evitar que Python genere archivos .pyc y use buffer
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Instalamos dependencias del sistema mínimas necesarias para compilar psycopg2 si fuera necesario
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Copiamos primero requirements para aprovechar la caché de capas de Docker
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copiamos el resto del código fuente del microservicio
COPY . .

# Creamos un usuario no-privileged para ejecución segura en contenedores cloud (Principio de menor privilegio)
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# Exponemos el puerto de la API REST
EXPOSE 8000

# Comando de arranque del servidor Uvicorn con FastAPI
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
