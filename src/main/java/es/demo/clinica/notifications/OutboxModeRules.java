package es.demo.clinica.notifications;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Reglas que debe cumplir cada modo de preparación de recordatorios. */
@Component
public class OutboxModeRules {

    private final boolean standardEnabled;
    private final long expressMaxHours;
    private final String groupedSender;

    public OutboxModeRules(
            @Value("${clinica.outbox.standard.enabled}") boolean standardEnabled,
            @Value("${clinica.outbox.express.max-hours}") long expressMaxHours,
            @Value("${clinica.outbox.grouped.sender:}") String groupedSender) {
        this.standardEnabled = standardEnabled;
        this.expressMaxHours = expressMaxHours;
        this.groupedSender = groupedSender;
    }

    /** Devuelve el motivo por el que el modo no puede usarse, o vacío si puede usarse. */
    public Optional<String> rejectionFor(String mode) {
        return switch (mode == null ? "" : mode) {
            case "standard" -> standardEnabled ? Optional.empty() : Optional.of("mode-disabled");
            case "express" -> expressMaxHours >= ReminderService.HORIZON.toHours()
                    ? Optional.empty()
                    : Optional.of("horizon-too-long");
            case "grouped" -> groupedSender.isBlank() ? Optional.of("sender-missing") : Optional.empty();
            default -> Optional.empty();
        };
    }
}
