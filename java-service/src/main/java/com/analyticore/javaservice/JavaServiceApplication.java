package com.analyticore.javaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ==============================================================================
 * CLASE PRINCIPAL DE ARRANQUE - SERVICIO DE ANÁLISIS (java-service)
 * ==============================================================================
 * Microservicio Worker construido con Spring Boot 3 y Java 21 bajo el patrón de
 * Arquitectura Limpia (Clean Architecture).
 */
@SpringBootApplication
public class JavaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaServiceApplication.class, args);
    }
}
