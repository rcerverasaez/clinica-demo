package es.demo.clinica.security;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Indica a todas las vistas si hay una sesión iniciada, para mostrar "Salir" o "Acceso personal". */
@ControllerAdvice
class CurrentUserAdvice {

    @ModelAttribute("loggedIn")
    boolean loggedIn(Principal principal) {
        return principal != null;
    }
}
