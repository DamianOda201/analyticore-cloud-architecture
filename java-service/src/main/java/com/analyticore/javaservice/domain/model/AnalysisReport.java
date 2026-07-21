package com.analyticore.javaservice.domain.model;

import java.util.List;
import java.util.Map;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Value Object (Resultados)
 * ==============================================================================
 * Objeto inmutable que encapsula el resultado del análisis de NLP:
 * - sentiment: polaridad, puntuación positiva/negativa.
 * - keywords: lista ordenada por relevancia de las palabras clave encontradas.
 */
public class AnalysisReport {
    private final Map<String, Object> sentiment;
    private final List<String> keywords;

    public AnalysisReport(Map<String, Object> sentiment, List<String> keywords) {
        this.sentiment = sentiment;
        this.keywords = keywords;
    }

    public Map<String, Object> getSentiment() {
        return sentiment;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
