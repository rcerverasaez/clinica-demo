package es.demo.clinica.booking;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.demo.clinica.FixedClockTestConfig;
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
class AppointmentApiTest {

    private static final String BOOKING = """
            {"treatmentId":"primera-visita","specialistId":"ana-lopez",
             "patientName":"Paciente API","patientEmail":"api@example.com",
             "start":"2030-01-07T10:00"}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    void listsTreatmentsPublicly() throws Exception {
        mvc.perform(get("/api/treatments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void createsAnAppointment() throws Exception {
        mvc.perform(post("/api/appointments").contentType("application/json").content(BOOKING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientName").value("Paciente API"));
    }

    @Test
    void returnsProblemDetailWhenTheRulesAreBroken() throws Exception {
        mvc.perform(post("/api/appointments").contentType("application/json").content(BOOKING));

        mvc.perform(post("/api/appointments").contentType("application/json").content(BOOKING))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Ese hueco ya está ocupado para este especialista."));
    }

    @Test
    void rejectsInvalidData() throws Exception {
        mvc.perform(post("/api/appointments").contentType("application/json")
                        .content("{\"patientEmail\":\"no-es-un-correo\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void onlyReceptionListsAppointments() throws Exception {
        mvc.perform(get("/api/appointments").with(user("recepcion").roles("RECEPCION")))
                .andExpect(status().isOk());
    }

    @Test
    void exposesOpenApiDocs() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/appointments']").exists());
    }
}
