import React, { useState } from 'react';

export default function SubmissionForm({ onSubmit, loading }) {
  const [text, setText] = useState(
    'La arquitectura orientada a servicios en la nube permite construir sistemas resilientes y altamente disponibles, desacoplando la capa de submisión web del procesamiento intensivo del backend.'
  );

  const handleSubmit = (e) => {
    e.preventDefault();
    if (text.trim().length < 5) return;
    onSubmit(text);
  };

  return (
    <div className="console-card">
      <div className="card-header">
        <span className="header-label">Canal de Ingesta y Orquestación REST (POST /api/v1/jobs)</span>
        <span className="header-meta">Endpoint: python-service:8000</span>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <textarea
            className="text-editor"
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="Escriba o pegue el texto para iniciar la evaluación asíncrona de polaridad y filtrado de tokens por frecuencia..."
            disabled={loading}
          />
          <div className="editor-footer">
            <span className="character-count">Longitud: {text.length} caracteres</span>
            <button type="submit" className="btn-execute" disabled={loading || text.trim().length < 5}>
              {loading ? 'Transmitiendo solicitud al API Gateway...' : 'Procesar en microservicios backend'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
