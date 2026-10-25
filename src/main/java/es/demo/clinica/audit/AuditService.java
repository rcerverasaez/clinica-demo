package es.demo.clinica.audit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registra las acciones de la clínica y permite consultar las últimas. */
@Service
@Transactional
public class AuditService {

    // Son claves de traducción (actor.NOMBRE): la vista del registro las muestra en el idioma del usuario.
    public static final String PUBLIC_ACTOR = "public";
    public static final String PATIENT_ACTOR = "patient";
    public static final String API_ACTOR = "api";
    public static final String SYSTEM_ACTOR = "system";

    private final AuditEventRepository events;
    private final Clock clock;

    public AuditService(AuditEventRepository events, Clock clock) {
        this.events = events;
        this.clock = clock;
    }

    public void record(String actor, AuditAction type, String target, String detail) {
        events.save(new AuditEvent(LocalDateTime.now(clock), actor, type, target, detail));
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> recent() {
        return events.findTop200ByOrderByOccurredAtDesc();
    }
}
