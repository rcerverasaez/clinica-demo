package es.demo.clinica.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.demo.clinica.catalog.ClinicCatalog;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppointmentServiceTest {

    // Lunes 7 de enero de 2030, 8:00: todas las fechas de los tests son posteriores.
    private static final Clock CLOCK = Clock.fixed(
            LocalDateTime.of(2030, 1, 7, 8, 0).atZone(ZoneId.of("Europe/Madrid")).toInstant(),
            ZoneId.of("Europe/Madrid"));

    private AppointmentService service;

    @BeforeEach
    void setUp() {
        ClinicCatalog catalog = new ClinicCatalog();
        service = new AppointmentService(catalog, new AppointmentRepository(), CLOCK);
    }

    @Test
    void booksAFreeSlot() {
        Appointment appointment = service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:00"));

        assertEquals("Primera visita", appointment.treatment().name());
        assertEquals(1, service.upcoming().size());
    }

    @Test
    void rejectsAnOverlappingSlotForTheSameSpecialist() {
        service.book(form("revision", "ana-lopez", "2030-01-07T10:00"));

        BookingException error = assertThrows(BookingException.class,
                () -> service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:30")));
        assertEquals("Ese hueco ya está ocupado para este especialista.", error.getMessage());
    }

    @Test
    void allowsTheSameSlotWithAnotherSpecialist() {
        service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:00"));

        Appointment appointment = service.book(form("primera-visita", "luis-garcia", "2030-01-07T10:00"));

        assertEquals("luis-garcia", appointment.specialist().id());
    }

    @Test
    void allowsAnAppointmentThatStartsWhenThePreviousOneEnds() {
        service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:00"));

        Appointment appointment = service.book(form("analitica", "ana-lopez", "2030-01-07T10:30"));

        assertEquals("2030-01-07T10:30", appointment.start().toString());
    }

    @Test
    void rejectsWeekends() {
        assertThrows(BookingException.class,
                () -> service.book(form("primera-visita", "ana-lopez", "2030-01-06T10:00")));
    }

    @Test
    void rejectsAppointmentsThatEndAfterClosing() {
        assertThrows(BookingException.class,
                () -> service.book(form("revision", "ana-lopez", "2030-01-07T17:30")));
    }

    @Test
    void rejectsStartsOutsideTheHalfHourGrid() {
        assertThrows(BookingException.class,
                () -> service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:15")));
    }

    @Test
    void rejectsPastDates() {
        assertThrows(BookingException.class,
                () -> service.book(form("primera-visita", "ana-lopez", "2030-01-07T07:00")));
    }

    private static BookingForm form(String treatmentId, String specialistId, String start) {
        BookingForm form = new BookingForm();
        form.setTreatmentId(treatmentId);
        form.setSpecialistId(specialistId);
        form.setPatientName("Paciente de prueba");
        form.setPatientEmail("paciente@example.com");
        form.setStart(LocalDateTime.parse(start));
        return form;
    }
}
