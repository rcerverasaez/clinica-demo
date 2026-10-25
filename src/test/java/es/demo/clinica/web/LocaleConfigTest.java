package es.demo.clinica.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.demo.clinica.FixedClockTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = "clinica.demo-data=false")
@AutoConfigureMockMvc
@Import(FixedClockTestConfig.class)
class LocaleConfigTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void spanishIsTheDefaultLanguage() throws Exception {
        mvc.perform(get("/reservar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("lang=\"es\"")))
                .andExpect(content().string(containsString("Reservar cita")))
                .andExpect(content().string(containsString("?lang=en")));
    }

    @Test
    void langParameterSwitchesToEnglishAndIsRemembered() throws Exception {
        MvcResult switched = mvc.perform(get("/reservar").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("clinica-idioma"))
                .andExpect(content().string(containsString("lang=\"en\"")))
                .andExpect(content().string(containsString("Book appointment")))
                .andReturn();

        mvc.perform(get("/reservar").cookie(switched.getResponse().getCookie("clinica-idioma")))
                .andExpect(content().string(containsString("Book appointment")));
    }

    @Test
    void validationMessagesFollowTheLanguage() throws Exception {
        mvc.perform(post("/reservar").with(csrf()).param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Choose a treatment.")));
    }
}
