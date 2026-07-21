"""
==============================================================================
ADAPTADOR DE CLIENTE HTTP (JavaAnalysisClientAdapter)
==============================================================================
Implementa IAnalysisServiceClientPort enviando una solicitud HTTP REST síncrona
al microservicio Java Worker (`java-service`).
Se configura la URL externa via variable de entorno (Patrón Cloud 12-Factor).
"""

import os
import httpx
from app.domain.clients import IAnalysisServiceClientPort

class JavaAnalysisClientAdapter(IAnalysisServiceClientPort):
    """
    Adaptador concreto para la comunicación REST inter-servicio (Python -> Java).
    """
    def __init__(self, java_service_url: str = None):
        # 12-Factor App: URL externa o fallback local para docker-compose
        self.java_service_url = java_service_url or os.getenv(
            "JAVA_SERVICE_URL", 
            "http://java-service:8081/api/v1/analyze"
        )

    def trigger_analysis(self, job_id: str, text: str) -> bool:
        """
        Realiza el POST síncrono enviando el jobId y texto al worker de Java.
        """
        payload = {
            "jobId": job_id,
            "text": text
        }
        try:
            # Enviamos con un timeout prudente para no bloquear el hilo de FastAPI indefinitely
            with httpx.Client(timeout=10.0) as client:
                response = client.post(self.java_service_url, json=payload)
                response.raise_for_status()
                print(f"[COMUNICACIÓN INTER-SERVICIO EXITOSA] Job {job_id} notificado a Java Worker.")
                return True
        except httpx.HTTPError as e:
            print(f"[ERROR DE COMUNICACIÓN REST Python -> Java]: {e}")
            raise e
