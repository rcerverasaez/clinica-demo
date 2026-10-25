package es.demo.clinica.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@AutoConfigureMockMvc
@Transactional
class CatalogControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void receptionSeesTheCatalog() throws Exception {
        mvc.perform(get("/recepcion/catalogo").with(user("recepcion").roles("RECEPCION")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Primera visita")));
    }

    @Test
    void receptionCanAddASpecialist() throws Exception {
        mvc.perform(post("/recepcion/catalogo/especialistas").with(csrf())
                        .with(user("recepcion").roles("RECEPCION"))
                        .param("name", "Dr. Pablo Mora")
                        .param("specialty", "Cardiología"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recepcion/catalogo"))
                .andExpect(flash().attribute("message", "message.catalog-saved"));
    }

    @Test
    void aRejectedTreatmentSaysWhy() throws Exception {
        mvc.perform(post("/recepcion/catalogo/consultas").with(csrf())
                        .with(user("recepcion").roles("RECEPCION"))
                        .param("name", "Test")
                        .param("durationMinutes", "3")
                        .param("price", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("bookingError", "catalog.duration.invalid"));
    }

    @Test
    void theSpecialistCannotChangeTheCatalog() throws Exception {
        mvc.perform(post("/recepcion/catalogo/especialistas").with(csrf())
                        .with(user("especialista").roles("ESPECIALISTA"))
                        .param("name", "Dr. Pablo Mora")
                        .param("specialty", "Cardiología"))
                .andExpect(status().isForbidden());
    }
}
