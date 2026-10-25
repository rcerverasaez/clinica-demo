package es.demo.clinica.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** Una acción registrada: quién, qué, sobre qué y cuándo. */
@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    private String id;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    private String actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private AuditAction type;

    private String target;

    private String detail;

    protected AuditEvent() {
    }

    public AuditEvent(LocalDateTime occurredAt, String actor, AuditAction type, String target, String detail) {
        this.id = UUID.randomUUID().toString();
        this.occurredAt = occurredAt;
        this.actor = actor;
        this.type = type;
        this.target = target;
        this.detail = detail;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getActor() {
        return actor;
    }

    public AuditAction getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getDetail() {
        return detail;
    }
}
