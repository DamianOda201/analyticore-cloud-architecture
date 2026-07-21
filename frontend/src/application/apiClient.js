/**
 * ==============================================================================
 * ARQUITECTURA LIMPIA - CAPA DE APLICACIÓN: Cliente API (ApiClient)
 * ==============================================================================
 * Abstracción sobre la API Fetch para desacoplar los componentes de UI de la
 * capa de red REST.
 */

const getBaseUrl = () => {
  if (import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  // Si estamos en producción dentro de Render (ej: analyticore-frontend.onrender.com)
  if (typeof window !== 'undefined' && window.location.hostname.includes('onrender.com')) {
    return window.location.origin.replace('-frontend', '-python-gateway') + '/api/v1/jobs';
  }
  // Fallback local (Nginx proxy en localhost)
  return '/api/v1/jobs';
};

export const ApiClient = {
  /**
   * Envía un nuevo texto al servicio Python y retorna el jobId.
   */
  async submitJob(text) {
    const baseUrl = getBaseUrl();
    const response = await fetch(baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ text })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.detail || 'Error al enviar el trabajo para análisis');
    }

    return response.json();
  },

  /**
   * Consulta el estado del trabajo usando su jobId (para Polling).
   */
  async getJobStatus(jobId) {
    const baseUrl = getBaseUrl();
    const response = await fetch(`${baseUrl}/${jobId}`);
    if (!response.ok) {
      throw new Error(`No se pudo obtener el estado del Job: ${jobId}`);
    }
    return response.json();
  }
};
