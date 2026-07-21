"""
==============================================================================
ARQUITECTURA LIMPIA - CAPA DE DOMINIO: Puertos para Clientes Externos
==============================================================================
Define el Puerto de Salida para la comunicación inter-servicio.
Nuestro caso de uso necesita notificar al Servicio de Análisis (Java) cuando entra
un nuevo trabajo. Para mantener Clean Architecture, abstraemos esa llamada HTTP.
"""

from abc import ABC, abstractmethod

class IAnalysisServiceClientPort(ABC):
    """
    Contrato para notificar al Worker de Java sobre un nuevo trabajo listo para procesar.
    """
    
    @abstractmethod
    def trigger_analysis(self, job_id: str, text: str) -> bool:
        """
        Envía una notificación síncrona o petición REST al servicio Java worker.
        Retorna True si el servicio aceptó la solicitud correctamente.
        """
        pass
