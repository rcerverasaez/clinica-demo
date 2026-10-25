package es.demo.clinica.web;

import es.demo.clinica.booking.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Vista de recepción: citas próximas y cancelación. */
@Controller
public class ReceptionController {

    private final AppointmentService appointments;

    public ReceptionController(AppointmentService appointments) {
        this.appointments = appointments;
    }

    @GetMapping("/recepcion")
    public String list(Model model) {
        model.addAttribute("appointments", appointments.upcoming());
        return "recepcion";
    }

    @PostMapping("/recepcion/{id}/cancelar")
    public String cancel(@PathVariable String id) {
        appointments.cancel(id);
        return "redirect:/recepcion";
    }
}
