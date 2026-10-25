package es.demo.clinica.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Comprueba las marcas de accesibilidad compartidas: enlace de salto, contenido principal y avisos anunciados. */
@SpringBootTest(properties = "clinica.demo-data=false")
@AutoConfigureMockMvc
class AccessibilityTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void everyPageHasASkipLinkToTheMainContent() throws Exception {
        mvc.perform(get("/reservar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"#contenido\"")))
                .andExpect(content().string(containsString("<main id=\"contenido\"")));
    }

    @Test
    void errorsAreAnnouncedToScreenReaders() throws Exception {
        mvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("role=\"alert\"")));
    }
}
