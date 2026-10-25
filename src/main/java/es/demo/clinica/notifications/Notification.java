package es.demo.clinica.notifications;

import es.demo.clinica.booking.Appointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** Mensaje en el buzón de salida. Es simulado: se guarda y se muestra en recepción, no se envía. */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String kind;

    private String recipient;

    private String subject;

    private String body;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    protected Notification() {
    }

    public Notification(Appointment appointment, String kind, String recipient, String subject, String body,
            LocalDateTime sentAt) {
        this.id = UUID.randomUUID().toString();
        this.appointment = appointment;
        this.kind = kind;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getKind() {
        return kind;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
