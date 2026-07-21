import React from 'react';

export default function Header() {
  return (
    <header className="top-header">
      <div className="header-identity">
        <h1 className="system-title">
          AnalytiCore Service Console
        </h1>
        <p className="system-description">
          Plataforma de procesamiento analítico distribuido en la nube. Orquestación y validación vía API Gateway Python (FastAPI), ejecución asíncrona de NLP en microservicio Worker Java (Spring Boot) y persistencia relacional transaccional en PostgreSQL.
        </p>
      </div>
      <div className="architecture-badges">
        <span className="badge-pill">Clean Architecture 12-Factor</span>
        <span className="badge-sub">SOA Polyglot Microservices</span>
      </div>
    </header>
  );
}
