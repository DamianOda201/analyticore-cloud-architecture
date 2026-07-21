"""
==============================================================================
ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Entidades y Modelos Puros
==============================================================================
En Arquitectura Limpia (Clean Architecture), la Capa de Dominio reside en el 
centro del sistema. Aquí definimos los objetos de negocio puros (Entities & 
Value Objects) sin depender de bibliotecas externas de persistencia ni web.
"""

from enum import Enum
from typing import Optional, List, Dict, Any
from datetime import datetime
from pydantic import BaseModel, Field

class JobStatus(str, Enum):
    """
    Value Object (Enum) que representa los estados del ciclo de vida de un trabajo.
    Cumple con el requisito de persistencia de estados:
    - PENDIENTE: Cuando el usuario envía el texto y se registra en base de datos.
    - PROCESANDO: Cuando el Worker de Java toma el trabajo y comienza el NLP.
    - COMPLETADO: Cuando el análisis finalizó exitosamente y se guardaron resultados.
    """
    PENDIENTE = "PENDIENTE"
    PROCESANDO = "PROCESANDO"
    COMPLETADO = "COMPLETADO"
    ERROR = "ERROR"

class JobEntity(BaseModel):
    """
    Entidad de Dominio puramente abstracta que representa el Trabajo de Análisis.
    Contiene las reglas e invariantes del negocio sobre qué datos conforman un Job.
    """
    id: str = Field(..., description="Identificador único UUID del trabajo (jobId)")
    text: str = Field(..., description="Texto original enviado por el usuario para análisis")
    status: JobStatus = Field(default=JobStatus.PENDIENTE, description="Estado actual del procesamiento")
    sentiment_result: Optional[Dict[str, Any]] = Field(default=None, description="Resultado de análisis de polaridad/sentimiento")
    keywords_result: Optional[List[str]] = Field(default=None, description="Lista de palabras clave extraídas")
    created_at: datetime = Field(default_factory=datetime.utcnow, description="Fecha y hora de creación del trabajo")
