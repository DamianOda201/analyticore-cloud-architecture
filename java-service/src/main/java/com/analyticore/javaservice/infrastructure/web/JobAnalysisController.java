package com.analyticore.javaservice.infrastructure.web;

import com.analyticore.javaservice.application.service.ProcessJobUseCaseImpl;
import com.analyticore.javaservice.infrastructure.web.dto.AnalyzeRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE INFRAESTRUCTURA: Adaptador Web REST Controller
 * ==============================================================================
 * Punto de entrada del Worker (Punto 3 de Sección 4 del Flujo de Datos):
 * Recibe la llamada síncrona/asíncrona desde el servicio Python (`POST /api/v1/analyze`)
 * y ejecuta el caso de uso en segundo plano o síncronamente.
 */
@RestController
@RequestMapping("/api/v1/analyze")
public class JobAnalysisController {

    private final ProcessJobUseCaseImpl processJobUseCase;

    public JobAnalysisController(ProcessJobUseCaseImpl processJobUseCase) {
        this.processJobUseCase = processJobUseCase;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receiveJob(@RequestBody AnalyzeRequestDto request) {
        if (request.getJobId() == null || request.getJobId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "jobId es requerido"));
        }

        System.out.println("[API JAVA WORKER] Solicitud de análisis recibida para Job: " + request.getJobId());

        // Ejecutamos en forma asíncrona dentro del JVM/Spring para no bloquear la respuesta HTTP
        // de notificación, pero orquestando inmediatamente el proceso en BD
        CompletableFuture.runAsync(() -> {
            processJobUseCase.execute(request.getJobId(), request.getText());
        });

        return ResponseEntity.accepted().body(Map.of(
                "status", "ACCEPTED",
                "jobId", request.getJobId(),
                "message", "El worker de Java ha comenzado a procesar el análisis en PostgreSQL."
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "java-analysis-worker"));
    }
}
