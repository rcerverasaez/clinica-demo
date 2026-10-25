package es.demo.clinica.booking;

import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reglas de la clínica: horario, días laborables, citas en el futuro y sin solapes. */
@Service
@Transactional
public class AppointmentService {

    private static final LocalTime OPENING = LocalTime.of(9, 0);
    private static final LocalTime CLOSING = LocalTime.of(18, 0);

    private final ClinicCatalog catalog;
    private final AppointmentRepository repository;
    private final Clock clock;

    public AppointmentService(ClinicCatalog catalog, AppointmentRepository repository, Clock clock) {
        this.catalog = catalog;
        this.repository = repository;
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
                treatment,
                specialist,
                form.getPatientName().trim(),
                form.getPatientEmail().trim(),
                start);
        return repository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Appointment> upcoming() {
        return repository.findByStartAtAfterOrderByStartAtAsc(LocalDateTime.now(clock));
    }

    public void cancel(String id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    private void validateSchedule(LocalDateTime start, LocalDateTime end) {
        if (!start.isAfter(LocalDateTime.now(clock))) {
            throw new BookingException("La cita tiene que ser en el futuro.");
        }
        if (start.getMinute() % 30 != 0) {
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
        if (repository.existsBySpecialistIdAndStartAtLessThanAndEndAtGreaterThan(
                specialist.getId(), end, start)) {
            throw new BookingException("Ese hueco ya está ocupado para este especialista.");
        }
    }
}
