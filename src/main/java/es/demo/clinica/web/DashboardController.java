package es.demo.clinica.web;

import es.demo.clinica.dashboard.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Panel de recepción con las cifras de la semana. */
@Controller
public class DashboardController {

    private final DashboardService dashboard;

    public DashboardController(DashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/recepcion/panel")
    public String panel(Model model) {
        model.addAttribute("panel", dashboard.panel());
        return "recepcion-panel";
    }
}
