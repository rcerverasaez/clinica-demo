package es.demo.clinica.web;

import es.demo.clinica.catalog.ClinicCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Página de inicio con las consultas y los especialistas. */
@Controller
public class HomeController {

    private final ClinicCatalog catalog;

    public HomeController(ClinicCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("treatments", catalog.treatments());
        model.addAttribute("specialists", catalog.specialists());
        return "index";
    }
}
