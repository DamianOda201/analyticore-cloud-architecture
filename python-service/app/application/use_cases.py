"""
==============================================================================
ARQUITECTURA LIMPIA - CAPA DE CASOS DE USO (Application Layer)
==============================================================================
Orquesta el flujo de negocio del servicio Python:
1. SubmitJobUseCase: Crea y persiste el Job con estado PENDIENTE y gatilla la API Java.
2. GetJobStatusUseCase: Recupera el estado y los resultados para el Polling del frontend.
"""

import uuid
from typing import Optional
from app.domain.models import JobEntity, JobStatus
from app.domain.repositories import IJobRepositoryPort
from app.domain.clients import IAnalysisServiceClientPort

class SubmitJobUseCase:
    """
    Caso de Uso responsable de procesar la llegada de un nuevo texto desde el Frontend.
    """
    def __init__(self, repository: IJobRepositoryPort, java_client: IAnalysisServiceClientPort):
        # Inversión de Dependencias: Recibimos puertos por inyección en el constructor
        self.repository = repository
        self.java_client = java_client

    def execute(self, text: str) -> JobEntity:
        """
        Flujo del caso de uso según Sección 4.2 del enunciado:
        1. Crea un registro en la base de datos con estado 'PENDIENTE'.
        2. Llama de forma síncrona al servicio Java para iniciar el análisis.
        3. Devuelve el JobId al frontend.
        """
        # 1. Generar nuevo UUID y crear entidad de dominio
        job_id = str(uuid.uuid4())
        job = JobEntity(
            id=job_id,
            text=text,
            status=JobStatus.PENDIENTE
        )

        # 2. Persistir en PostgreSQL de forma Stateless via Repositorio
        saved_job = self.repository.save(job)

        # 3. Llamar al servicio Java para iniciar el análisis (Síncrono/Orquestado)
        try:
            self.java_client.trigger_analysis(job_id=saved_job.id, text=saved_job.text)
        except Exception as e:
            # Si el servicio Java falla temporalmente, loggeamos o marcamos error según resiliencia
            print(f"[ADVERTENCIA ARQUITECTÓNICA] Falló notificación inicial a Java Worker: {e}")
            # Mantenemos PENDIENTE para que pueda ser reintentado por polling o colas si se expanda

        return saved_job


class GetJobStatusUseCase:
    """
    Caso de Uso consultado periódicamente por el Frontend (Polling) según Sección 4.5.
    """
    def __init__(self, repository: IJobRepositoryPort):
        self.repository = repository

    def execute(self, job_id: str) -> Optional[JobEntity]:
        """Consulta el estado en la base de datos externa (PostgreSQL)."""
        return self.repository.get_by_id(job_id)
