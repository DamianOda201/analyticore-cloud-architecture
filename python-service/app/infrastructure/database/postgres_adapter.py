"""
==============================================================================
ADAPTADOR DE PERSISTENCIA (PostgresJobRepositoryAdapter)
==============================================================================
Implementa la interfaz IJobRepositoryPort usando SQLAlchemy para conectarse
a la base de datos externa PostgreSQL (cumpliendo con el patrón Stateless en la
memoria de la aplicación y externalización en Render DB).
"""

import os
import json
from typing import Optional
from sqlalchemy import create_engine, Column, String, Text, DateTime
from sqlalchemy.orm import declarative_base, sessionmaker, Session
from app.domain.models import JobEntity, JobStatus
from app.domain.repositories import IJobRepositoryPort

Base = declarative_base()

class JobSQLModel(Base):
    """Modelo ORM de SQLAlchemy mapeado a la tabla externa 'jobs' en PostgreSQL."""
    __tablename__ = "jobs"

    id = Column(String(36), primary_key=True, index=True)
    text = Column(Text, nullable=False)
    status = Column(String(50), default=JobStatus.PENDIENTE.value)
    sentiment_result = Column(Text, nullable=True)  # Guardado como JSON text o plain
    keywords_result = Column(Text, nullable=True)   # Guardado como JSON text
    created_at = Column(DateTime, nullable=False)


class PostgresJobRepositoryAdapter(IJobRepositoryPort):
    """
    Adaptador concreto que traduce operaciones de entidades de dominio pura
    hacia consultas relacionales SQL en PostgreSQL.
    """
    def __init__(self, db_url: Optional[str] = None):
        # 12-Factor App: Leemos configuración desde variable de entorno o construimos desde variables DB_
        if not db_url:
            raw_url = os.getenv("DATABASE_URL")
            if raw_url:
                if raw_url.startswith("postgres://"):
                    raw_url = raw_url.replace("postgres://", "postgresql://", 1)
                db_url = raw_url
            else:
                db_host = os.getenv("DB_HOST", "postgres")
                db_port = os.getenv("DB_PORT", "5432")
                db_name = os.getenv("DB_NAME", "analyticore")
                db_user = os.getenv("DB_USER", "postgres")
                db_password = os.getenv("DB_PASSWORD", "postgres")
                db_url = f"postgresql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"
                
        self.db_url = db_url
        self.engine = create_engine(self.db_url, pool_pre_ping=True)
        self.SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=self.engine)
        
        # Creamos la tabla automáticamente para facilitar la inicialización del prototipo
        Base.metadata.create_all(bind=self.engine)

    def _get_session(self) -> Session:
        return self.SessionLocal()

    def save(self, job: JobEntity) -> JobEntity:
        session = self._get_session()
        try:
            db_job = session.query(JobSQLModel).filter(JobSQLModel.id == job.id).first()
            if not db_job:
                db_job = JobSQLModel(
                    id=job.id,
                    text=job.text,
                    status=job.status.value,
                    sentiment_result=json.dumps(job.sentiment_result) if job.sentiment_result else None,
                    keywords_result=json.dumps(job.keywords_result) if job.keywords_result else None,
                    created_at=job.created_at
                )
                session.add(db_job)
            else:
                db_job.status = job.status.value
                if job.sentiment_result:
                    db_job.sentiment_result = json.dumps(job.sentiment_result)
                if job.keywords_result:
                    db_job.keywords_result = json.dumps(job.keywords_result)
            session.commit()
            session.refresh(db_job)
            return job
        finally:
            session.close()

    def get_by_id(self, job_id: str) -> Optional[JobEntity]:
        session = self._get_session()
        try:
            db_job = session.query(JobSQLModel).filter(JobSQLModel.id == job_id).first()
            if not db_job:
                return None
            
            return JobEntity(
                id=db_job.id,
                text=db_job.text,
                status=JobStatus(db_job.status),
                sentiment_result=json.loads(db_job.sentiment_result) if db_job.sentiment_result else None,
                keywords_result=json.loads(db_job.keywords_result) if db_job.keywords_result else None,
                created_at=db_job.created_at
            )
        finally:
            session.close()

    def update_status(self, job_id: str, status: JobStatus) -> bool:
        session = self._get_session()
        try:
            db_job = session.query(JobSQLModel).filter(JobSQLModel.id == job_id).first()
            if db_job:
                db_job.status = status.value
                session.commit()
                return True
            return False
        finally:
            session.close()
