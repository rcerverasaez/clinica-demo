package es.demo.clinica.notifications;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Ejecuta el envío de recordatorios cada hora. Se puede desactivar con clinica.reminders.enabled=false. */
@Component
@ConditionalOnProperty(name = "clinica.reminders.enabled", havingValue = "true", matchIfMissing = true)
class ReminderScheduler {

    private final ReminderService reminders;

    ReminderScheduler(ReminderService reminders) {
        this.reminders = reminders;
    }

    @Scheduled(fixedDelayString = "${clinica.reminders.every-ms:3600000}",
            initialDelayString = "${clinica.reminders.initial-delay-ms:10000}")
    void run() {
        reminders.sendDueReminders();
    }
}
