package com.analyticore.javaservice.domain.port;

import com.analyticore.javaservice.domain.model.Job;
import java.util.Optional;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Puerto de Persistencia (Output Port)
 * ==============================================================================
 * Contrato que el adaptador JPA deberá implementar para guardar y consultar
 * los trabajos en PostgreSQL.
 */
public interface JobRepositoryPort {
    Optional<Job> findById(String id);
    Job save(Job job);
}
