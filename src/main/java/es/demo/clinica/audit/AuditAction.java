package es.demo.clinica.audit;

/**
 * Tipos de acción que quedan registradas. El texto que se ve en pantalla está en messages.properties,
 * con la clave audit.action.NOMBRE.
 */
public enum AuditAction {
    BOOKING,
    CANCELLATION,
    MESSAGE,
    DOCUMENT_UPLOAD,
    STATUS_CHANGE,
    ABSENCE,
    REMINDERS
}
