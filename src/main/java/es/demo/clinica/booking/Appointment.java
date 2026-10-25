package es.demo.clinica.booking;

import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import java.time.LocalDateTime;

/** Cita confirmada. */
public record Appointment(
        String id,
        Treatment treatment,
        Specialist specialist,
        String patientName,
        String patientEmail,
        LocalDateTime start) {

    public LocalDateTime end() {
        return start.plusMinutes(treatment.durationMinutes());
    }

    /** Indica si el intervalo dado se solapa con esta cita. */
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end());
    }
}
