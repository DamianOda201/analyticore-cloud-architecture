# Prompts Especializados para Lucidchart AI - Plataforma "AnalytiCore"

Este documento contiene los prompts exactos y completos diseñados para ser ingresados en la herramienta **Lucidchart AI** (`AI -> Generate Diagram`). 

A diferencia de PlantUML, que suele amontonar los elementos e identificar patrones automáticos poco estéticos, estos prompts están elaborados en **lenguaje descriptivo UML estructurado con separación explícita por contenedores (swimlanes/boxes)**. Esto guía a la inteligencia artificial de Lucidchart para generar diagramas profesionales, limpios, organizados jerárquicamente y dignos de una presentación universitaria o ejecutiva de alto nivel.

---

## 1. Diagrama de Componentes (Arquitectura General en la Nube)

**Instrucción para el estudiante:** Copia el siguiente bloque de texto tal cual y pégalo en el generador de Lucidchart AI seleccionando el tipo de diagrama **"UML Component Diagram"** o **"System Architecture Diagram"**.

```text
Create a clean, highly organized, professional UML Component Diagram for a cloud-native service-oriented architecture named "AnalytiCore Platform".
Ensure wide spacing between containers to prevent visual clutter and overlap. Use modern boxes with rounded corners and clear labels.

Structure the diagram into distinct external and cloud environment boundaries:

1. Boundary: "Client Layer (External)"
   - [Actor: End User / Browser] -> Represents the user accessing the web application.

2. Boundary: "Cloud Platform (Render / Production Environment)"
   Contains 4 separate Docker containers and 1 Managed Database:
   
   Container Box 1: "Frontend Container (/frontend)"
   - [Component: Nginx Web Server] -> Serves static files and acts as lightweight web server.
   - [Component: React SPA] -> Single Page Application UI where users submit text and view keyword/sentiment analysis results.
   
   Container Box 2: "Submission Service Container (/python-service)"
   - [Component: Python REST API Gateway & Orchestrator] -> Built with FastAPI. Validates requests, persists initial jobs, and orchestrates synchronous analysis triggers.
   
   Container Box 3: "Worker Analysis Container (/java-service)"
   - [Component: Java Worker Service] -> Built with Spring Boot 21. Processes text tasks, performs sentiment analysis, extracts keywords, and updates job completion status.
   
   Database Box: "Managed Storage"
   - [Database: PostgreSQL (Stateless Storage)] -> Stores the 'jobs' table containing job ID, text, status (PENDIENTE, PROCESANDO, COMPLETADO), and JSON analysis results.

Connections and Data Flow (Draw arrows with clear directional labels):
1. [Actor: End User / Browser] --> (HTTPS / Web Traffic) --> [Component: Nginx Web Server]
2. [Component: React SPA] --> (POST /api/v1/jobs & GET /api/v1/jobs/{id}) --> [Component: Python REST API Gateway & Orchestrator]
3. [Component: Python REST API Gateway & Orchestrator] --> (Synchronous REST POST /api/v1/analyze with jobId) --> [Component: Java Worker Service]
4. [Component: Python REST API Gateway & Orchestrator] <--> (Read/Write Job Status & Results via TCP/SSL) <--> [Database: PostgreSQL (Stateless Storage)]
5. [Component: Java Worker Service] <--> (Update Status & Save Results via JDBC/SSL) <--> [Database: PostgreSQL (Stateless Storage)]

Layout instructions: Place Client Layer at the top/left, Python and Java services in the middle layer side-by-side with generous horizontal padding, and PostgreSQL Database at the bottom. Use clean arrows without crossed lines.
```

---

## 2. Diagrama de Capas - Servicio de Submisión (`python-service`)

**Instrucción para el estudiante:** Pégalo en Lucidchart AI seleccionando **"Layered Architecture Diagram"** o **"UML Class/Package Diagram"**.

```text
Create a clean, well-spaced Layered Architecture Diagram illustrating Clean Architecture (Hexagonal / Ports and Adapters) for the Python Submission Service ("python-service").
Organize the diagram using nested horizontal layers from top (Infrastructure/Interface Adapters) pointing inward to the core (Domain). Use distinct color shades for each layer to clearly separate responsibilities.

Layer 1 (Outer / Top Layer): "Infrastructure & Interface Adapters Layer"
- [Component: FastAPI REST Controllers] -> Handles incoming HTTP requests from React Frontend (POST /jobs, GET /jobs/{id}).
- [Component: PostgresJobRepositoryAdapter] -> Implements persistence logic using SQLAlchemy/database drivers.
- [Component: JavaAnalysisClientAdapter] -> Implements HTTP client (Httpx/Requests) to synchronously call the Java Worker REST API.

Layer 2 (Middle Layer): "Application / Use Cases Layer"
- [Component: SubmitJobUseCase] -> Orchestrates job creation: validates text, saves initial PENDIENTE status via repository port, and triggers Java analysis via client port.
- [Component: GetJobStatusUseCase] -> Retrieves job status and completed analysis results via repository port.

Layer 3 (Inner Core / Bottom Layer): "Domain Layer (Pure Python Core - No External Dependencies)"
- [Entity: JobEntity] -> Core business model representing ID, text, status, sentiment, and keywords.
- [Value Object: JobStatus] -> Enumeration (PENDIENTE, PROCESANDO, COMPLETADO).
- [Interface Port: IJobRepositoryPort] -> Abstract contract for database operations.
- [Interface Port: IAnalysisServiceClientPort] -> Abstract contract for external worker notification.

Dependency Rule Arrows (Crucial: All arrows must point inward toward the Domain Layer):
- [FastAPI REST Controllers] --> depends on --> [SubmitJobUseCase] & [GetJobStatusUseCase]
- [PostgresJobRepositoryAdapter] -- implements (dashed arrow) --> [IJobRepositoryPort]
- [JavaAnalysisClientAdapter] -- implements (dashed arrow) --> [IAnalysisServiceClientPort]
- [SubmitJobUseCase] & [GetJobStatusUseCase] --> depends on --> [IJobRepositoryPort] & [IAnalysisServiceClientPort] & [JobEntity]

Layout: Vertical stack of 3 distinct boxes with wide vertical spacing. Ensure no arrows point outward from Domain or Application to Infrastructure.
```

---

## 3. Diagrama de Capas - Servicio de Análisis Worker (`java-service`)

**Instrucción para el estudiante:** Pégalo en Lucidchart AI seleccionando **"Layered Architecture Diagram"** o **"UML Package Diagram"**.

```text
Create a clean, structured Layered Architecture Diagram demonstrating Clean Architecture for the Java Spring Boot Worker Service ("java-service").
Display the layers hierarchically with clear boundaries and generous spacing so text is legible and boxes do not overlap.

Layer 1 (Outer / Top Layer): "Infrastructure & Web Adapters"
- [Component: JobAnalysisController] -> Spring Boot REST Controller listening to POST /api/v1/analyze from the Python service.
- [Component: JpaJobRepositoryAdapter] -> Spring Data JPA / JDBC adapter managing database connections to PostgreSQL.

Layer 2 (Middle Layer): "Application & Domain Services Layer"
- [Component: ProcessJobUseCaseImpl] -> Application service that coordinates job processing: fetches job, sets status to PROCESANDO, executes analysis, and saves completion.
- [Component: SimpleTextAnalyzerService] -> Domain service implementing text analysis: polarity calculation (positive/neutral/negative) and keyword frequency extraction without stopwords.

Layer 3 (Inner Core / Bottom Layer): "Domain Core Layer (Pure Java - Zero Framework Dependencies)"
- [Entity: Job] -> Pure domain object with business invariants.
- [Value Object: AnalysisReport] -> Immutable value object encapsulating sentiment score and keyword list.
- [Interface Port: JobRepositoryPort] -> Input/Output boundary for job persistence.
- [Interface Port: TextAnalyzerPort] -> Boundary interface for NLP processing.

Dependency & Implementation Arrows (All dependencies strictly point toward Domain Core):
- [JobAnalysisController] --> invokes --> [ProcessJobUseCaseImpl]
- [JpaJobRepositoryAdapter] -- implements (dashed) --> [JobRepositoryPort]
- [SimpleTextAnalyzerService] -- implements (dashed) --> [TextAnalyzerPort]
- [ProcessJobUseCaseImpl] --> uses --> [JobRepositoryPort] & [TextAnalyzerPort] & [Job]

Layout: Top-down vertical flow with distinct color-coded boxes for Outer Layer, Application Layer, and Domain Core.
```

---

## 4. Diagrama de Capas - Frontend Web (`frontend`)

**Instrucción para el estudiante:** Pégalo en Lucidchart AI seleccionando **"Layered Architecture Diagram"** o **"Modular UI Diagram"**.

```text
Create a clean, modern Layered Architecture Diagram representing the modular, Clean Architecture approach applied to a Single Page Application (SPA) built with React and Vite ("frontend").
Arrange the diagram horizontally or vertically with clear separation between UI, Business Hooks, and Domain definitions.

Layer 1 (Presentation / UI Layer): "Presentation Layer (React Components)"
- [Component: App & DashboardView] -> Main layout, header, and container view.
- [Component: JobSubmissionForm] -> Text area and submit button with input validation.
- [Component: AnalysisResultsCard] -> Visual cards displaying Sentiment badge and top Keywords pills.
- [Component: StatusLoader] -> Dynamic indicator showing PENDIENTE or PROCESANDO states.

Layer 2 (Application / Custom Hooks Layer): "Application Layer (State & Custom Hooks)"
- [Custom Hook: useJobSubmission] -> Encapsulates API submission logic and error handling.
- [Custom Hook: useJobTracker] -> Implements smart polling mechanism querying status every 2 seconds until COMPLETADO.
- [Service: ApiClient] -> Abstraction over Fetch API communicating with the Python API Gateway.

Layer 3 (Domain / Models Layer): "Domain & UI Contracts Layer"
- [Models: Job & AnalysisResult Types] -> Data schemas and state interfaces.
- [Constants: JobStatusEnum] -> Immutable status definitions (PENDIENTE, PROCESANDO, COMPLETADO).

Data Flow & Dependency Arrows:
- [Presentation Components] --> calls --> [useJobSubmission] & [useJobTracker]
- [useJobSubmission] & [useJobTracker] --> invokes --> [ApiClient]
- [ApiClient] & Hooks --> use definitions from --> [Domain Models & Constants]

Layout: Clear, wide boxes with smooth orthogonal lines connecting UI components to Application Hooks down to Domain models.
```
