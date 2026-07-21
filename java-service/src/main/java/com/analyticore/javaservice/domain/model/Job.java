package com.analyticore.javaservice.domain.model;

import java.time.LocalDateTime;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Entidad Pura (Job)
 * ==============================================================================
 * Entidad principal que representa un trabajo de análisis en memoria de negocio.
 * Cero dependencias de JPA, Hibernate ni Spring Boot.
 */
public class Job {
    private String id;
    private String text;
    private JobStatus status;
    private AnalysisReport report;
    private LocalDateTime createdAt;

    public Job(String id, String text, JobStatus status, AnalysisReport report, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.status = status;
        this.report = report;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public AnalysisReport getReport() { return report; }
    public void setReport(AnalysisReport report) { this.report = report; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
