package es.demo.clinica.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.demo.clinica.FixedClockTestConfig;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.booking.BookingForm;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@Transactional
@Import(FixedClockTestConfig.class)
class MessageServiceTest {

    @Autowired
    private MessageService messages;

    @Autowired
    private AppointmentService appointments;

    @Test
    void keepsTheConversationInOrderForBothSides() {
        Appointment booked = book();

        messages.send(booked.getId(), MessageAuthor.PACIENTE, "¿Tengo que venir en ayunas?");
        messages.send(booked.getId(), MessageAuthor.RECEPCION, "Sí, en ayunas de 8 horas.");

        assertEquals(2, messages.forAppointment(booked.getId()).size());
        assertEquals(MessageAuthor.PACIENTE, messages.forAppointment(booked.getId()).get(0).getAuthor());
    }

    @Test
    void rejectsEmptyAndTooLongMessages() {
        Appointment booked = book();

        assertThrows(BookingException.class,
                () -> messages.send(booked.getId(), MessageAuthor.PACIENTE, "   "));
        assertThrows(BookingException.class,
                () -> messages.send(booked.getId(), MessageAuthor.PACIENTE, "a".repeat(501)));
    }

    private Appointment book() {
        BookingForm form = new BookingForm();
        form.setTreatmentId("primera-visita");
        form.setSpecialistId("ana-lopez");
        form.setPatientName("Paciente de prueba");
        form.setPatientEmail("paciente@example.com");
        form.setStart(LocalDateTime.parse("2030-01-07T10:00"));
        return appointments.book(form);
    }
}
