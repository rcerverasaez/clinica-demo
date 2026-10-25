package es.demo.clinica.notifications;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentRepository;
import es.demo.clinica.booking.AppointmentStatus;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Recordatorios de las citas confirmadas que empiezan en las próximas 24 horas. */
@Service
@Transactional
public class ReminderService {

    static final String KIND = "RECORDATORIO";
    static final Duration HORIZON = Duration.ofHours(24);

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AppointmentRepository appointments;
    private final NotificationRepository notifications;
    private final Clock clock;
    private final AuditService audit;

    public ReminderService(AppointmentRepository appointments, NotificationRepository notifications, Clock clock,
            AuditService audit) {
        this.appointments = appointments;
        this.notifications = notifications;
        this.clock = clock;
        this.audit = audit;
    }

    /** Devuelve cuántos recordatorios nuevos se han guardado en el buzón. */
    public int sendDueReminders() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Appointment> due = appointments.findByStatusAndStartAtBetweenOrderByStartAtAsc(
                AppointmentStatus.CONFIRMADA, now, now.plus(HORIZON));
        int sent = 0;
        for (Appointment appointment : due) {
            if (notifications.existsByAppointmentIdAndKind(appointment.getId(), KIND)) {
                continue;
            }
            notifications.save(new Notification(appointment, KIND, appointment.getPatientEmail(),
                    subject(appointment), body(appointment), now));
            log.info("Recordatorio simulado guardado para la cita {}", appointment.getId());
            sent++;
        }
        if (sent > 0) {
            audit.record(AuditService.SYSTEM_ACTOR, AuditAction.REMINDERS, null, "Recordatorios nuevos: " + sent);
        }
        return sent;
    }

    @Transactional(readOnly = true)
    public List<Notification> outbox() {
        return notifications.findAllByOrderBySentAtDesc();
    }

    private static String subject(Appointment appointment) {
        return "Recordatorio de tu cita del " + WHEN.format(appointment.getStartAt());
    }

    private static String body(Appointment appointment) {
        return "Hola, " + appointment.getPatientName() + ". Te recordamos tu cita de "
                + appointment.getTreatment().getName() + " con " + appointment.getSpecialist().getName()
                + " el " + WHEN.format(appointment.getStartAt()) + ". "
                + "Si no puedes asistir, cancélala desde Mis citas.";
    }
}
