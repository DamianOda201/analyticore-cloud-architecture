# Informe Ejecutivo: Prototipo de Arquitectura Cloud Políglota "AnalytiCore"

**Para:** Dirección Ejecutiva y Comité Técnico de AnalytiCore  
**Asunto:** Viabilidad Técnica y Valor de la Arquitectura orientada a Servicios en la Nube (SOA)  
**Fecha:** Julio 2026  

---

### 1. El Problema de Negocio que se Resuelve
AnalytiCore, como startup emergente en el análisis de datos masivos, enfrenta el reto de ofrecer un servicio web en línea capaz de recibir textos y procesarlos para obtener **análisis de sentimiento** y **extracción de palabras clave** en tiempos de respuesta óptimos. Las arquitecturas tradicionales monolíticas presentan serias limitaciones: cuando el volumen de textos crece drásticamente, el sistema entero se ralentiza, sufriendo caídas generalizadas y bloqueando la interfaz visual del usuario. Además, un monolito en un solo lenguaje impide que el equipo adopte las mejores herramientas especializadas para cada función (ej. librerías de NLP robustas vs. frameworks ágiles de APIs o interfaces dinámicas).

### 2. La Solución Propuesta y su Valor Empresarial
Para resolver esta problemática y sentar las bases de una expansión masiva, se implementó un **prototipo funcional en la nube bajo una arquitectura orientada a servicios (SOA)**, contenerizada y desplegada en **Render**. La plataforma se estructura en tres componentes principales desacoplados y empaquetados independientemente mediante **Docker**:

1. **Frontend Web (React + Nginx):** Una aplicación de página única (SPA) ligera, rápida y moderna que ofrece una experiencia de usuario interactiva e inmediata sin recargas.
2. **Servicio de Submisión (Python / FastAPI):** Actúa como puerta de entrada (API Gateway y orquestador). Recibe la solicitud, valida la información, registra inmediatamente el trabajo con estado `"PENDIENTE"` en la base de datos y delega el análisis de forma síncrona/asíncrona, devolviendo un identificador (`jobId`) para que el usuario nunca experimente bloqueos ni tiempos de espera congelados.
3. **Servicio de Análisis Worker (Java / Spring Boot):** Un microservicio de alta eficiencia computacional dedicado exclusivamente al procesamiento intensivo del texto (cálculo de polaridad y extracción frecuencial de palabras clave), actualizando el estado del trabajo en tiempo real a `"PROCESANDO"` y `"COMPLETADO"`.

Toda la comunicación transcurre mediante contratos limpios de **APIs RESTful**, y el estado de los trabajos se externaliza completamente hacia una base de datos **PostgreSQL administrada y sin estado local (Stateless)** en los servicios de aplicación.

### 3. Beneficios Estratégicos de la Arquitectura Elegida (En Lenguaje Sencillo)

* **Escalabilidad Elástica e Independiente:** Al separar la interfaz (React), la recepción de trabajos (Python) y el procesamiento de texto (Java) en contenedores individuales, podemos escalar de forma aislada solo el componente que esté bajo presión. Si miles de usuarios envían textos complejos simultáneamente, multiplicamos automáticamente las instancias del *Worker de Java* sin duplicar costos ni recursos en la interfaz visual ni en el servicio web de entrada.
* **Mantenibilidad y Arquitectura Limpia:** Cada microservicio fue diseñado siguiendo el patrón de **Arquitectura Limpia (Clean Architecture)**, dividiendo estrictamente el código en tres capas: *Dominio* (reglas puras del negocio), *Casos de Uso* (lógica de coordinación) y *Adaptadores de Infraestructura* (bases de datos y controladores web). Esto significa que si en el futuro queremos cambiar la base de datos o reemplazar la librería de análisis, se modifica únicamente un adaptador externo sin tocar ni arriesgar el corazón de la aplicación.
* **Flexibilidad del Equipo y Poliglotismo Tecnológico:** Usar diferentes lenguajes de programación permite a la startup aprovechar el "mejor instrumento para cada tarea": **React** para interfaces fluidas, **Python** por su agilidad inigualable para APIs y ecosistema de datos, y **Java** por su robustez, control de memoria, concurrencia industrial y velocidad en procesos pesados de back-office. Asimismo, los equipos de ingeniería pueden trabajar en paralelo en distintos repositorios o carpetas sin interferir entre sí.

**Conclusión:** El prototipo AnalytiCore demuestra un éxito rotundo en viabilidad técnica, ofreciendo una solución resiliente, fácil de mantener y preparada para crecer de forma global en la nube sin reescrituras estructurales.
