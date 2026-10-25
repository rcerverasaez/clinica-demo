package es.demo.clinica.web;

import es.demo.clinica.audit.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Últimas acciones registradas, para recepción. */
@Controller
public class AuditController {

    private final AuditService audit;

    public AuditController(AuditService audit) {
        this.audit = audit;
    }

    @GetMapping("/recepcion/auditoria")
    public String events(Model model) {
        model.addAttribute("events", audit.recent());
        return "recepcion-auditoria";
    }
}
