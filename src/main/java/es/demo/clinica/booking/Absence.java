package es.demo.clinica.booking;

import es.demo.clinica.catalog.Specialist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** Tramo en que un especialista no atiende. */
@Entity
@Table(name = "absences")
public class Absence {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    private String reason;

    protected Absence() {
    }

    public Absence(Specialist specialist, LocalDateTime startAt, LocalDateTime endAt, String reason) {
        this.id = UUID.randomUUID().toString();
        this.specialist = specialist;
        this.startAt = startAt;
        this.endAt = endAt;
        this.reason = reason;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getReason() {
        return reason;
    }
}
