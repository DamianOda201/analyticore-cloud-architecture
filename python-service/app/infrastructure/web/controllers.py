"""
==============================================================================
CONTROLADORES REST (Interface Adapters / Web)
==============================================================================
Reciben peticiones HTTP desde el Frontend (React), validan los esquemas Pydantic
e invocan los Casos de Uso (SubmitJobUseCase, GetJobStatusUseCase).
"""

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any

from app.domain.models import JobEntity, JobStatus
from app.application.use_cases import SubmitJobUseCase, GetJobStatusUseCase
from app.infrastructure.database.postgres_adapter import PostgresJobRepositoryAdapter
from app.infrastructure.external.java_client_adapter import JavaAnalysisClientAdapter

router = APIRouter(prefix="/api/v1/jobs", tags=["Jobs API"])

# DTOs de Pydantic para Entrada y Salida REST
class SubmitJobRequest(BaseModel):
    text: str = Field(..., min_length=3, description="Texto sobre el cual se realizará el análisis de sentimiento y palabras clave")

class JobResponse(BaseModel):
    jobId: str
    text: str
    status: str
    sentiment: Optional[Dict[str, Any]] = None
    keywords: Optional[List[str]] = None


# Inyección de Dependencias (Dependency Injection Container en FastAPI)
def get_submit_use_case():
    repository = PostgresJobRepositoryAdapter()
    java_client = JavaAnalysisClientAdapter()
    return SubmitJobUseCase(repository=repository, java_client=java_client)

def get_status_use_case():
    repository = PostgresJobRepositoryAdapter()
    return GetJobStatusUseCase(repository=repository)


@router.post("", response_model=JobResponse, status_code=status.HTTP_201_CREATED)
def submit_job(
    request: SubmitJobRequest,
    use_case: SubmitJobUseCase = Depends(get_submit_use_case)
):
    """
    Punto 4.2 del Flujo de Datos: El frontend envía el texto. 
    Crea registro con estado PENDIENTE, llama al servicio Java y devuelve el jobId.
    """
    try:
        job = use_case.execute(text=request.text)
        return JobResponse(
            jobId=job.id,
            text=job.text,
            status=job.status.value,
            sentiment=job.sentiment_result,
            keywords=job.keywords_result
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error al procesar y orquestar el trabajo: {str(e)}"
        )


@router.get("/{job_id}", response_model=JobResponse)
def get_job_status(
    job_id: str,
    use_case: GetJobStatusUseCase = Depends(get_status_use_case)
):
    """
    Punto 4.5 del Flujo de Datos: El frontend consulta periódicamente usando el jobId
    para conocer el estado y obtener los resultados.
    """
    job = use_case.execute(job_id=job_id)
    if not job:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Trabajo con jobId '{job_id}' no encontrado en PostgreSQL."
        )
    
    return JobResponse(
        jobId=job.id,
        text=job.text,
        status=job.status.value,
        sentiment=job.sentiment_result,
        keywords=job.keywords_result
    )
