package es.demo.clinica.booking;

import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Cita confirmada. */
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_email")
    private String patientEmail;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    protected Appointment() {
    }

    public Appointment(String id, Treatment treatment, Specialist specialist, String patientName,
            String patientEmail, LocalDateTime startAt) {
        this.id = id;
        this.treatment = treatment;
        this.specialist = specialist;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.startAt = startAt;
        this.endAt = startAt.plusMinutes(treatment.getDurationMinutes());
    }

    public String getId() {
        return id;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }
}
