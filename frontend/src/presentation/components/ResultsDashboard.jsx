import React from 'react';

export default function ResultsDashboard({ job }) {
  if (!job || !job.sentiment || !job.keywords) return null;

  const { polarity, score, positivesFound, negativesFound } = job.sentiment;

  return (
    <div className="metrics-grid">
      <div className="console-card">
        <div className="card-header">
          <span className="header-label">Evaluación de Polaridad</span>
          <span className="header-meta">Métrica NLP</span>
        </div>
        <div className="card-body">
          <table className="data-table">
            <tbody>
              <tr>
                <td className="label-col">Clasificación:</td>
                <td className="value-col">
                  <span className={`polarity-tag ${polarity}`}>{polarity}</span>
                </td>
              </tr>
              <tr>
                <td className="label-col">Puntuación Neta:</td>
                <td className="value-col">{score > 0 ? `+${score}` : score}</td>
              </tr>
              <tr>
                <td className="label-col">Aciertos Positivos:</td>
                <td className="value-col" style={{ color: '#4ade80' }}>{positivesFound}</td>
              </tr>
              <tr>
                <td className="label-col">Aciertos Negativos:</td>
                <td className="value-col" style={{ color: '#f87171' }}>{negativesFound}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div className="console-card">
        <div className="card-header">
          <span className="header-label">Tokens Identificados (Frecuencia TF sin Stopwords)</span>
          <span className="header-meta">Filtro Léxico</span>
        </div>
        <div className="card-body">
          <div className="tokens-container">
            {job.keywords && job.keywords.length > 0 ? (
              job.keywords.map((kw, idx) => (
                <span key={idx} className="token-chip">
                  {kw}
                </span>
              ))
            ) : (
              <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                No se registraron tokens significativos tras la eliminación de palabras vacías (stopwords).
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
