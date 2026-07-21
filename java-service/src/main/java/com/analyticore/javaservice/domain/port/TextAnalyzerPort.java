package com.analyticore.javaservice.domain.port;

import com.analyticore.javaservice.domain.model.AnalysisReport;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Puerto de Procesamiento NLP
 * ==============================================================================
 * Interfaz abstracta para procesar textos y generar los reportes de sentimiento
 * y palabras clave.
 */
public interface TextAnalyzerPort {
    AnalysisReport analyze(String text);
}
