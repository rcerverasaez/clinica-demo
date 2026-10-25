package es.demo.clinica.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;

import es.demo.clinica.FixedClockTestConfig;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingForm;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = { "clinica.demo-data=false", "clinica.reminders.enabled=false" })
@Transactional
@Import(FixedClockTestConfig.class)
class ReminderServiceTest {

    @Autowired
    private ReminderService reminders;

    @Autowired
    private AppointmentService appointments;

    @Test
    void aReminderIsSavedOnceForAppointmentsInTheNext24Hours() {
        book("2030-01-07T10:00");

        assertEquals(1, reminders.sendDueReminders());
        assertEquals(0, reminders.sendDueReminders());
        assertEquals(1, reminders.outbox().size());
    }

    @Test
    void appointmentsFurtherAwayAreNotRemindedYet() {
        book("2030-01-08T10:00");

        assertEquals(0, reminders.sendDueReminders());
    }

    @Test
    void cancelledAppointmentsAreNotReminded() {
        Appointment booked = book("2030-01-07T10:00");
        appointments.cancel(booked.getId());

        assertEquals(0, reminders.sendDueReminders());
    }

    private Appointment book(String start) {
        BookingForm form = new BookingForm();
        form.setTreatmentId("primera-visita");
        form.setSpecialistId("ana-lopez");
        form.setPatientName("Paciente de prueba");
        form.setPatientEmail("paciente@example.com");
        form.setStart(LocalDateTime.parse(start));
        return appointments.book(form);
    }
}
