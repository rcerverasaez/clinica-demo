package es.demo.clinica.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.demo.clinica.FixedClockTestConfig;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingForm;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@AutoConfigureMockMvc
@Transactional
@Import(FixedClockTestConfig.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppointmentService appointments;

    @Test
    void theCodeIsSentInTheBodyAndNeverInTheUrl() throws Exception {
        Appointment booked = book();

        mvc.perform(post("/mis-citas").with(csrf()).param("codigo", booked.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Primera visita")));

        mvc.perform(get("/mis-citas").param("codigo", booked.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Primera visita"))));
    }

    @Test
    void theCodeCancelsTheAppointment() throws Exception {
        Appointment booked = book();

        mvc.perform(post("/mis-citas/cancelar").with(csrf()).param("codigo", booked.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tu cita se ha cancelado.")));
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
