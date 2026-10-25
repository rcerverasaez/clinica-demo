package es.demo.clinica.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.demo.clinica.FixedClockTestConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@Transactional
@Import(FixedClockTestConfig.class)
class AppointmentServiceTest {

    @Autowired
    private AppointmentService service;

    @Test
    void booksAFreeSlot() {
        Appointment appointment = service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:00"));

        assertEquals("Primera visita", appointment.getTreatment().getName());
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

        assertEquals("luis-garcia", appointment.getSpecialist().getId());
    }

    @Test
    void allowsAnAppointmentThatStartsWhenThePreviousOneEnds() {
        service.book(form("primera-visita", "ana-lopez", "2030-01-07T10:00"));

        Appointment appointment = service.book(form("analitica", "ana-lopez", "2030-01-07T10:30"));

        assertEquals("2030-01-07T10:30", appointment.getStartAt().toString());
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
