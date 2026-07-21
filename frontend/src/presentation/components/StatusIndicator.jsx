import React from 'react';
import { JOB_STATUS } from '../../domain/types.js';

export default function StatusIndicator({ job }) {
  if (!job) return null;

  const getStatusText = (status) => {
    switch (status) {
      case JOB_STATUS.PENDIENTE:
        return 'Estado: PENDIENTE — Registro creado en PostgreSQL. Transmitiendo notificación síncrona al worker Java...';
      case JOB_STATUS.PROCESANDO:
        return 'Estado: PROCESANDO — Microservicio Java en ejecución: calculando polaridad y filtrando stopwords...';
      case JOB_STATUS.COMPLETADO:
        return 'Estado: COMPLETADO — Análisis finalizado e indexado en la base de datos transaccional.';
      case JOB_STATUS.ERROR:
        return 'Estado: ERROR — Se ha producido una excepción durante el procesamiento en el backend.';
      default:
        return `Estado actual: ${status}`;
    }
  };

  const isProcessing = job.status === JOB_STATUS.PENDIENTE || job.status === JOB_STATUS.PROCESANDO;

  return (
    <div className={`status-bar ${job.status}`}>
      <div className="status-info">
        <span className="job-id-code">ID Transacción: {job.jobId}</span>
        <span className="status-state-text">{getStatusText(job.status)}</span>
      </div>
      {isProcessing && <div className="loader-ring" />}
    </div>
  );
}
