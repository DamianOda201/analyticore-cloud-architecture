"""
==============================================================================
PUNTO DE ENTRADA PRINCIPAL - SERVICIO DE SUBMISIÓN (python-service)
==============================================================================
Configura la aplicación FastAPI, activa el middleware CORS (para permitir
comunicación desde el contenedor de Frontend Nginx) y monta los enrutadores REST.
"""

import os
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv

from app.infrastructure.web.controllers import router as jobs_router

# Cargamos variables de entorno (.env si existe en entorno local)
load_dotenv()

app = FastAPI(
    title="AnalytiCore - Servicio de Submisión (Python/FastAPI)",
    description="Microservicio Gateway y Orquestador de Análisis en Arquitectura Limpia",
    version="1.0.0"
)

# Configuración CORS para el Frontend (React servido por Nginx en puerto 3000 u 80)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción cloud estricta, restrigir al dominio de Render del frontend
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Montaje de Rutas API
app.include_router(jobs_router)

@app.get("/health", tags=["Healthcheck"])
def healthcheck():
    """Endpoint para comprobación de estado e integraciones en Render/Docker."""
    return {"status": "UP", "service": "python-submission-service"}
