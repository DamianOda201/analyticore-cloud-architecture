package com.analyticore.javaservice.infrastructure.persistence.adapter;

import com.analyticore.javaservice.domain.model.AnalysisReport;
import com.analyticore.javaservice.domain.model.Job;
import com.analyticore.javaservice.domain.model.JobStatus;
import com.analyticore.javaservice.domain.port.JobRepositoryPort;
import com.analyticore.javaservice.infrastructure.persistence.entity.JobJpaEntity;
import com.analyticore.javaservice.infrastructure.persistence.repository.SpringDataJobRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE INFRAESTRUCTURA: Adaptador de Persistencia JPA
 * ==============================================================================
 * Implementa JobRepositoryPort traduciendo entre la entidad pura del Dominio (Job)
 * y la entidad ORM de la tabla 'jobs' de PostgreSQL (JobJpaEntity).
 */
@Component
public class JpaJobRepositoryAdapter implements JobRepositoryPort {

    private final SpringDataJobRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public JpaJobRepositoryAdapter(SpringDataJobRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Job> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Job save(Job job) {
        JobJpaEntity entity = toEntity(job);
        JobJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Job toDomain(JobJpaEntity entity) {
        try {
            JobStatus status = JobStatus.valueOf(entity.getStatus());
            AnalysisReport report = null;

            if (entity.getSentimentResult() != null || entity.getKeywordsResult() != null) {
                Map<String, Object> sentimentMap = null;
                List<String> keywordsList = null;

                if (entity.getSentimentResult() != null && !entity.getSentimentResult().isEmpty()) {
                    sentimentMap = objectMapper.readValue(entity.getSentimentResult(), new TypeReference<Map<String, Object>>() {});
                }
                if (entity.getKeywordsResult() != null && !entity.getKeywordsResult().isEmpty()) {
                    keywordsList = objectMapper.readValue(entity.getKeywordsResult(), new TypeReference<List<String>>() {});
                }
                report = new AnalysisReport(sentimentMap, keywordsList);
            }

            return new Job(entity.getId(), entity.getText(), status, report, entity.getCreatedAt());
        } catch (Exception e) {
            System.err.println("[ADAPTADOR JPA JAVA] Error al convertir entidad JPA a Dominio: " + e.getMessage());
            return new Job(entity.getId(), entity.getText(), JobStatus.ERROR, null, entity.getCreatedAt());
        }
    }

    private JobJpaEntity toEntity(Job job) {
        try {
            String sentimentJson = null;
            String keywordsJson = null;

            if (job.getReport() != null) {
                if (job.getReport().getSentiment() != null) {
                    sentimentJson = objectMapper.writeValueAsString(job.getReport().getSentiment());
                }
                if (job.getReport().getKeywords() != null) {
                    keywordsJson = objectMapper.writeValueAsString(job.getReport().getKeywords());
                }
            }

            return new JobJpaEntity(
                    job.getId(),
                    job.getText(),
                    job.getStatus().name(),
                    sentimentJson,
                    keywordsJson,
                    job.getCreatedAt()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar el reporte de análisis a JSON: " + e.getMessage(), e);
        }
    }
}
