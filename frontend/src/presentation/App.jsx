import React from 'react';
import Header from './components/Header.jsx';
import SubmissionForm from './components/SubmissionForm.jsx';
import StatusIndicator from './components/StatusIndicator.jsx';
import ResultsDashboard from './components/ResultsDashboard.jsx';
import { useJobTracker } from '../application/useJobTracker.js';

export default function App() {
  const { currentJob, loading, error, startJob } = useJobTracker();

  return (
    <div className="console-wrapper">
      <Header />
      
      {error && (
        <div className="status-bar ERROR" style={{ marginBottom: '1.5rem' }}>
          <div className="status-info">
            <span className="job-id-code">Excepción del Sistema</span>
            <span className="status-state-text">{error}</span>
          </div>
        </div>
      )}

      <SubmissionForm onSubmit={startJob} loading={loading} />
      
      <StatusIndicator job={currentJob} />
      
      {currentJob && currentJob.status === 'COMPLETADO' && (
        <ResultsDashboard job={currentJob} />
      )}
    </div>
  );
}
