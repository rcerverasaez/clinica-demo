package es.demo.clinica.audit;

/** Tipos de acción que quedan registradas. */
public enum AuditAction {
    BOOKING("Reserva"),
    CANCELLATION("Cancelación"),
    MESSAGE("Mensaje"),
    DOCUMENT_UPLOAD("Documento subido"),
    STATUS_CHANGE("Cambio de estado"),
    ABSENCE("Ausencia"),
    REMINDERS("Recordatorios");

    private final String label;

    AuditAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
