"""
==============================================================================
ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Puertos de Repositorio (Inversión de Dependencias)
==============================================================================
Este archivo define el Puerto (Port) de salida para la persistencia.
Aplicamos el Principio de Inversión de Dependencias (DIP de SOLID):
Los Casos de Uso (Application) dependerán ÚNICAMENTE de esta interfaz abstracta, 
nunca de la implementación concreta de PostgreSQL o SQLAlchemy.
"""

from abc import ABC, abstractmethod
from typing import Optional
from app.domain.models import JobEntity, JobStatus

class IJobRepositoryPort(ABC):
    """
    Contrato abstracto para el acceso a datos del trabajo de análisis.
    El adaptador de infraestructura que conecte con PostgreSQL deberá implementar estos métodos.
    """
    
    @abstractmethod
    def save(self, job: JobEntity) -> JobEntity:
        """Persiste un nuevo trabajo o actualiza uno existente en la base de datos."""
        pass

    @abstractmethod
    def get_by_id(self, job_id: str) -> Optional[JobEntity]:
        """Consulta un trabajo por su ID único."""
        pass

    @abstractmethod
    def update_status(self, job_id: str, status: JobStatus) -> bool:
        """Actualiza el estado del trabajo."""
        pass
