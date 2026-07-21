package com.analyticore.javaservice.application.service;

import com.analyticore.javaservice.domain.model.AnalysisReport;
import com.analyticore.javaservice.domain.port.TextAnalyzerPort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE APLICACIÓN/DOMINIO: Servicio de Análisis NLP
 * ==============================================================================
 * Implementa TextAnalyzerPort calculando polaridad simple positiva/negativa
 * y extrayendo palabras clave por frecuencia de aparición (excluyendo stopwords).
 */
@Service
public class SimpleTextAnalyzerService implements TextAnalyzerPort {

    private static final Set<String> STOPWORDS = Set.copyOf(Arrays.asList(
            "el", "la", "los", "las", "un", "una", "unos", "unas", "y", "o", "a", "ante",
            "bajo", "cabe", "con", "contra", "de", "desde", "en", "entre", "hacia", "hasta",
            "para", "por", "según", "sin", "sobre", "tras", "que", "es", "son", "fue",
            "era", "ser", "como", "esta", "este", "esto", "ese", "esa", "muy", "más",
            "su", "sus", "del", "al", "lo", "se", "me", "nos", "mi", "mis", "tu", "tus",
            "the", "and", "or", "to", "in", "of", "for", "with", "on", "at", "by", "is", "are"
    ));

    private static final Set<String> POSITIVE_WORDS = Set.copyOf(Arrays.asList(
            "excelente", "bueno", "genial", "increíble", "maravilloso", "perfecto",
            "ágil", "eficiente", "rápido", "innovador", "feliz", "éxito", "óptimo",
            "great", "excellent", "good", "amazing", "wonderful", "perfect", "fast", "efficient"
    ));

    private static final Set<String> NEGATIVE_WORDS = Set.copyOf(Arrays.asList(
            "malo", "pésimo", "lento", "error", "fallo", "problema", "terrible",
            "complicado", "difícil", "caída", "inestable", "bloqueo",
            "bad", "slow", "problem", "fail", "broken"
    ));

    @Override
    public AnalysisReport analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            Map<String, Object> neutral = new HashMap<>();
            neutral.put("polarity", "NEUTRAL");
            neutral.put("score", 0);
            return new AnalysisReport(neutral, Collections.emptyList());
        }

        // Tokenización básica en minúsculas y sin puntuación
        String[] words = text.toLowerCase().replaceAll("[^a-záéíóúüñ0-9\\s]", "").split("\\s+");

        int posCount = 0;
        int negCount = 0;
        Map<String, Integer> wordFrequencies = new HashMap<>();

        for (String w : words) {
            if (w.isEmpty() || STOPWORDS.contains(w) || w.length() < 3) {
                continue;
            }

            // Conteo de frecuencias para keywords
            wordFrequencies.put(w, wordFrequencies.getOrDefault(w, 0) + 1);

            // Conteo de polaridad
            if (POSITIVE_WORDS.contains(w)) posCount++;
            if (NEGATIVE_WORDS.contains(w)) negCount++;
        }

        // Cálculo de sentimiento
        String polarity = "NEUTRAL";
        int netScore = posCount - negCount;
        if (netScore > 0) polarity = "POSITIVO";
        else if (netScore < 0) polarity = "NEGATIVO";

        Map<String, Object> sentiment = new HashMap<>();
        sentiment.put("polarity", polarity);
        sentiment.put("score", netScore);
        sentiment.put("positivesFound", posCount);
        sentiment.put("negativesFound", negCount);

        // Extracción de hasta las 8 palabras clave más frecuentes
        List<String> topKeywords = wordFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new AnalysisReport(sentiment, topKeywords);
    }
}
