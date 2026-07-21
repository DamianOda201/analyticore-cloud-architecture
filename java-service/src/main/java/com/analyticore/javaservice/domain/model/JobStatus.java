package com.analyticore.javaservice.domain.model;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Value Object Enum (Estados)
 * ==============================================================================
 * Representa los estados del ciclo de procesamiento en el Worker.
 */
public enum JobStatus {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO,
    ERROR
}
