package es.demo.clinica.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "clinica.demo-data=false")
@AutoConfigureMockMvc
class SecurityRulesTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void bookingRequiresLogin() throws Exception {
        mvc.perform(get("/reservar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/reservar").with(user("vbote@vbote.com").roles("RECEPCION")))
                .andExpect(status().isOk());
    }

    @Test
    void homeRedirectsToLogin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void staticImagesArePublic() throws Exception {
        mvc.perform(get("/img/hero-appointment.svg")).andExpect(status().isOk());
    }

    @Test
    void theStaffLoginPageIsShown() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void receptionRequiresLogin() throws Exception {
        mvc.perform(get("/recepcion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void receptionIsForReceptionStaffOnly() throws Exception {
        mvc.perform(get("/recepcion").with(user("recepcion").roles("RECEPCION")))
                .andExpect(status().isOk());
        mvc.perform(get("/recepcion").with(user("especialista").roles("ESPECIALISTA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserCanLogIn() throws Exception {
        mvc.perform(formLogin("/login").user("vbote@vbote.com").password("Hola.001"))
                .andExpect(authenticated().withRoles("RECEPCION", "ESPECIALISTA"));
    }
}
