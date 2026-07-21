package com.analyticore.javaservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE INFRAESTRUCTURA: Entidad JPA (PostgreSQL)
 * ==============================================================================
 * Mapeo ORM hacia la misma tabla 'jobs' de PostgreSQL que utiliza Python,
 * demostrando el acceso externo compartido y Stateless.
 */
@Entity
@Table(name = "jobs")
public class JobJpaEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "sentiment_result", columnDefinition = "TEXT")
    private String sentimentResult;

    @Column(name = "keywords_result", columnDefinition = "TEXT")
    private String keywordsResult;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public JobJpaEntity() {}

    public JobJpaEntity(String id, String text, String status, String sentimentResult, String keywordsResult, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.status = status;
        this.sentimentResult = sentimentResult;
        this.keywordsResult = keywordsResult;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSentimentResult() { return sentimentResult; }
    public void setSentimentResult(String sentimentResult) { this.sentimentResult = sentimentResult; }

    public String getKeywordsResult() { return keywordsResult; }
    public void setKeywordsResult(String keywordsResult) { this.keywordsResult = keywordsResult; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
