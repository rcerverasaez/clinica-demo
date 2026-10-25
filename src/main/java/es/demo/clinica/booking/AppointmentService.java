package es.demo.clinica.booking;

import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reglas de la clínica: horario, días laborables, citas en el futuro, sin solapes y sin ausencias. */
@Service
@Transactional
public class AppointmentService {

    private static final LocalTime OPENING = LocalTime.of(9, 0);
    private static final LocalTime CLOSING = LocalTime.of(18, 0);
    private static final int SLOT_MINUTES = 30;

    private final ClinicCatalog catalog;
    private final AppointmentRepository repository;
    private final AbsenceRepository absences;
    private final Clock clock;

    public AppointmentService(ClinicCatalog catalog, AppointmentRepository repository, AbsenceRepository absences,
            Clock clock) {
        this.catalog = catalog;
        this.repository = repository;
        this.absences = absences;
        this.clock = clock;
    }

    public Appointment book(BookingForm form) {
        Treatment treatment = catalog.treatment(form.getTreatmentId())
                .orElseThrow(() -> new BookingException("La consulta elegida no existe."));
        Specialist specialist = catalog.specialist(form.getSpecialistId())
                .orElseThrow(() -> new BookingException("El especialista elegido no existe."));

        LocalDateTime start = form.getStart();
        LocalDateTime end = start.plusMinutes(treatment.getDurationMinutes());
        validateSchedule(start, end);
        validateFreeSlot(specialist, start, end);

        Appointment appointment = new Appointment(
                UUID.randomUUID().toString(),
                newCode(),
                treatment,
                specialist,
                form.getPatientName().trim(),
                form.getPatientEmail().trim(),
                start);
        return repository.save(appointment);
    }

    /** Huecos reservables de un especialista para una consulta en un día concreto. */
    @Transactional(readOnly = true)
    public List<LocalDateTime> availableSlots(String specialistId, String treatmentId, LocalDate date) {
        Treatment treatment = catalog.treatment(treatmentId).orElse(null);
        Specialist specialist = catalog.specialist(specialistId).orElse(null);
        if (treatment == null || specialist == null) {
            return List.of();
        }
        List<LocalDateTime> slots = new ArrayList<>();
        for (LocalDateTime start = date.atTime(OPENING); start.toLocalTime().isBefore(CLOSING);
                start = start.plusMinutes(SLOT_MINUTES)) {
            LocalDateTime end = start.plusMinutes(treatment.getDurationMinutes());
            if (isBookable(specialist, start, end)) {
                slots.add(start);
            }
        }
        return slots;
    }

    @Transactional(readOnly = true)
    public List<Appointment> upcoming() {
        return repository.findByStatusAndStartAtAfterOrderByStartAtAsc(
                AppointmentStatus.CONFIRMADA, LocalDateTime.now(clock));
    }

    @Transactional(readOnly = true)
    public List<Appointment> onDay(LocalDate date) {
        return repository.findByStartAtBetweenOrderByStartAtAsc(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public Appointment findByCode(String code) {
        return repository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new BookingException("No encontramos ninguna cita con ese código."));
    }

    /** Cancelar lo cambia a CANCELADA: la cita se conserva para el historial. */
    public void cancel(String id) {
        repository.findById(id).ifPresent(a -> a.changeStatus(AppointmentStatus.CANCELADA));
    }

    /** El paciente cancela con su código, siempre que la cita siga vigente y no haya empezado. */
    public Appointment cancelByCode(String code) {
        Appointment appointment = findByCode(code);
        if (appointment.getStatus() != AppointmentStatus.CONFIRMADA
                || !appointment.getStartAt().isAfter(LocalDateTime.now(clock))) {
            throw new BookingException("Esta cita ya no se puede cancelar.");
        }
        appointment.changeStatus(AppointmentStatus.CANCELADA);
        return appointment;
    }

    /** Recepción o especialista marcan la cita como atendida o como no presentada. */
    public void changeStatus(String id, AppointmentStatus status) {
        if (status == AppointmentStatus.CONFIRMADA || status == AppointmentStatus.CANCELADA) {
            throw new BookingException("Ese estado no se puede asignar desde la agenda.");
        }
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new BookingException("La cita no existe."));
        if (appointment.getStatus() != AppointmentStatus.CONFIRMADA) {
            throw new BookingException("Solo se puede cambiar el estado de una cita confirmada.");
        }
        appointment.changeStatus(status);
    }

    public Absence addAbsence(String specialistId, LocalDateTime start, LocalDateTime end, String reason) {
        Specialist specialist = catalog.specialist(specialistId)
                .orElseThrow(() -> new BookingException("El especialista elegido no existe."));
        if (!end.isAfter(start)) {
            throw new BookingException("La ausencia tiene que terminar después de empezar.");
        }
        return absences.save(new Absence(specialist, start, end, reason.trim()));
    }

    private boolean isBookable(Specialist specialist, LocalDateTime start, LocalDateTime end) {
        try {
            validateSchedule(start, end);
            validateFreeSlot(specialist, start, end);
            return true;
        } catch (BookingException e) {
            return false;
        }
    }

    private void validateSchedule(LocalDateTime start, LocalDateTime end) {
        if (!start.isAfter(LocalDateTime.now(clock))) {
            throw new BookingException("La cita tiene que ser en el futuro.");
        }
        if (start.getMinute() % SLOT_MINUTES != 0) {
            throw new BookingException("Las citas empiezan en punto o y media.");
        }
        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BookingException("No abrimos los fines de semana.");
        }
        boolean endsSameDay = end.toLocalDate().equals(start.toLocalDate());
        if (start.toLocalTime().isBefore(OPENING) || !endsSameDay
                || end.toLocalTime().isAfter(CLOSING)) {
            throw new BookingException("La cita tiene que caber en el horario de 9:00 a 18:00.");
        }
    }

    private void validateFreeSlot(Specialist specialist, LocalDateTime start, LocalDateTime end) {
        if (repository.existsBySpecialistIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                specialist.getId(), AppointmentStatus.CONFIRMADA, end, start)) {
            throw new BookingException("Ese hueco ya está ocupado para este especialista.");
        }
        if (absences.existsBySpecialistIdAndStartAtLessThanAndEndAtGreaterThan(specialist.getId(), end, start)) {
            throw new BookingException("El especialista no atiende en ese horario.");
        }
    }

    private static String newCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private static String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }
}
