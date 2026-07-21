import { useState, useEffect, useRef } from 'react';
import { ApiClient } from './apiClient.js';
import { JOB_STATUS } from '../domain/types.js';

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE APLICACIÓN: Custom Hook (Polling & Tracker)
 * ==============================================================================
 * Orquesta la consulta periódica (polling) solicitada en la sección 4.5 del
 * Flujo de Datos, consultando hasta recibir el estado COMPLETADO.
 */
export function useJobTracker() {
  const [currentJob, setCurrentJob] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const pollingInterval = useRef(null);

  const startJob = async (text) => {
    setLoading(true);
    setError(null);
    setCurrentJob(null);
    if (pollingInterval.current) clearInterval(pollingInterval.current);

    try {
      // 1. Enviar solicitud inicial REST
      const initialJob = await ApiClient.submitJob(text);
      setCurrentJob(initialJob);

      // 2. Si aún no está completado, iniciar polling inteligente cada 2 segundos
      if (initialJob.status !== JOB_STATUS.COMPLETADO && initialJob.status !== JOB_STATUS.ERROR) {
        pollingInterval.current = setInterval(async () => {
          try {
            const updated = await ApiClient.getJobStatus(initialJob.jobId);
            setCurrentJob(updated);

            // Detener polling al finalizar
            if (updated.status === JOB_STATUS.COMPLETADO || updated.status === JOB_STATUS.ERROR) {
              clearInterval(pollingInterval.current);
              setLoading(false);
            }
          } catch (err) {
            console.error('Error durante el polling:', err);
          }
        }, 2000);
      } else {
        setLoading(false);
      }
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    return () => {
      if (pollingInterval.current) clearInterval(pollingInterval.current);
    };
  }, []);

  return {
    currentJob,
    loading,
    error,
    startJob
  };
}
