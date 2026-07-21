package com.analyticore.javaservice.application.service;

import com.analyticore.javaservice.domain.model.AnalysisReport;
import com.analyticore.javaservice.domain.model.Job;
import com.analyticore.javaservice.domain.model.JobStatus;
import com.analyticore.javaservice.domain.port.JobRepositoryPort;
import com.analyticore.javaservice.domain.port.TextAnalyzerPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE CASOS DE USO: Orquestador de Procesamiento Worker
 * ==============================================================================
 * Al recibir el jobId desde la llamada REST de Python, orquesta el ciclo:
 * 1. Consulta el trabajo en PostgreSQL usando JobRepositoryPort.
 * 2. Actualiza estado a "PROCESANDO" (para visibilidad si se realiza polling inmediato).
 * 3. Ejecuta el análisis NLP via TextAnalyzerPort.
 * 4. Persiste el resultado final en la base de datos externa cambiando el estado a "COMPLETADO".
 */
@Service
public class ProcessJobUseCaseImpl {

    private final JobRepositoryPort repositoryPort;
    private final TextAnalyzerPort textAnalyzerPort;

    // Inyección de dependencias por constructor (Inversión de dependencias)
    public ProcessJobUseCaseImpl(JobRepositoryPort repositoryPort, TextAnalyzerPort textAnalyzerPort) {
        this.repositoryPort = repositoryPort;
        this.textAnalyzerPort = textAnalyzerPort;
    }

    public void execute(String jobId, String fallbackText) {
        System.out.println("[WORKER JAVA] Iniciando procesamiento del Job: " + jobId);

        // 1. Obtener de BD o crear temporal desde el fallback transmitido
        Optional<Job> optionalJob = repositoryPort.findById(jobId);
        Job job;
        if (optionalJob.isPresent()) {
            job = optionalJob.get();
        } else {
            System.out.println("[ADVERTENCIA] Job " + jobId + " no existía en BD aún. Creando registro desde el texto transmitido.");
            job = new Job(jobId, fallbackText != null ? fallbackText : "", JobStatus.PENDIENTE, null, LocalDateTime.now());
        }

        // 2. Cambiar a PROCESANDO en la base de datos externa Stateless
        job.setStatus(JobStatus.PROCESANDO);
        repositoryPort.save(job);

        try {
            // Simular breve tiempo computacional de procesamiento si se desea o procesar directamente
            // 3. Ejecutar análisis del texto
            AnalysisReport report = textAnalyzerPort.analyze(job.getText());

            // 4. Actualizar entidad de dominio y persistir con COMPLETADO
            job.setReport(report);
            job.setStatus(JobStatus.COMPLETADO);
            repositoryPort.save(job);

            System.out.println("[WORKER JAVA] Job " + jobId + " COMPLETADO exitosamente.");
        } catch (Exception e) {
            System.err.println("[WORKER JAVA ERROR] Falló el análisis del Job " + jobId + ": " + e.getMessage());
            job.setStatus(JobStatus.ERROR);
            repositoryPort.save(job);
        }
    }
}
