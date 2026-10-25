package es.demo.clinica.documents;

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
class DocumentServiceTest {

    private static final byte[] PDF = {1, 2, 3};

    @Autowired
    private DocumentService documents;

    @Autowired
    private AppointmentService appointments;

    @Test
    void thePatientDownloadsTheDocumentWithTheCodeOfItsAppointment() {
        Appointment booked = book("2030-01-07T10:00");
        Document uploaded = documents.upload(booked.getId(), "analitica.pdf", "application/pdf", PDF, "especialista");

        Document downloaded = documents.downloadForCode(uploaded.getId(), booked.getCode());

        assertEquals("analitica.pdf", downloaded.getFileName());
        assertEquals(1, documents.forAppointment(booked.getId()).size());
    }

    @Test
    void aCodeFromAnotherAppointmentDoesNotOpenTheDocument() {
        Appointment first = book("2030-01-07T10:00");
        Appointment second = book("2030-01-07T11:00");
        Document uploaded = documents.upload(first.getId(), "analitica.pdf", "application/pdf", PDF, "especialista");

        assertThrows(BookingException.class, () -> documents.downloadForCode(uploaded.getId(), second.getCode()));
    }

    @Test
    void onlyPdfJpgAndPngAreAccepted() {
        Appointment booked = book("2030-01-07T10:00");

        assertThrows(BookingException.class,
                () -> documents.upload(booked.getId(), "notas.txt", "text/plain", PDF, "especialista"));
    }

    @Test
    void filesOverTwoMegabytesAreRejected() {
        Appointment booked = book("2030-01-07T10:00");

        assertThrows(BookingException.class, () -> documents.upload(booked.getId(), "grande.pdf",
                "application/pdf", new byte[(int) DocumentService.MAX_BYTES + 1], "especialista"));
    }

    @Test
    void emptyFilesAreRejected() {
        Appointment booked = book("2030-01-07T10:00");

        assertThrows(BookingException.class,
                () -> documents.upload(booked.getId(), "vacio.pdf", "application/pdf", new byte[0], "especialista"));
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
