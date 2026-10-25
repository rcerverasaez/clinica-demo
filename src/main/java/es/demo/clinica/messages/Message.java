package es.demo.clinica.messages;

import es.demo.clinica.booking.Appointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** Mensaje de la conversación de una cita. */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    private MessageAuthor author;

    private String body;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Message() {
    }

    public Message(Appointment appointment, MessageAuthor author, String body, LocalDateTime createdAt) {
        this.id = UUID.randomUUID().toString();
        this.appointment = appointment;
        this.author = author;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public MessageAuthor getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
