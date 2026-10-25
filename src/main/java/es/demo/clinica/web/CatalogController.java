package es.demo.clinica.web;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Altas de especialistas y consultas desde recepción. */
@Controller
public class CatalogController {

    private final ClinicCatalog catalog;
    private final AuditService audit;

    public CatalogController(ClinicCatalog catalog, AuditService audit) {
        this.catalog = catalog;
        this.audit = audit;
    }

    @GetMapping("/recepcion/catalogo")
    public String catalog(Model model) {
        model.addAttribute("specialists", catalog.specialists());
        model.addAttribute("treatments", catalog.treatments());
        return "recepcion-catalogo";
    }

    @PostMapping("/recepcion/catalogo/especialistas")
    public String addSpecialist(@RequestParam String name, @RequestParam String specialty, Principal principal,
            RedirectAttributes redirect) {
        try {
            Specialist specialist = catalog.addSpecialist(name, specialty);
            audit.record(principal.getName(), AuditAction.CATALOG, specialist.getId(),
                    "audit.detail.catalog-specialist");
            redirect.addFlashAttribute("message", "message.catalog-saved");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/recepcion/catalogo";
    }

    @PostMapping("/recepcion/catalogo/consultas")
    public String addTreatment(@RequestParam String name, @RequestParam int durationMinutes,
            @RequestParam BigDecimal price, Principal principal, RedirectAttributes redirect) {
        try {
            Treatment treatment = catalog.addTreatment(name, durationMinutes, price);
            audit.record(principal.getName(), AuditAction.CATALOG, treatment.getId(),
                    "audit.detail.catalog-treatment");
            redirect.addFlashAttribute("message", "message.catalog-saved");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/recepcion/catalogo";
    }
}
