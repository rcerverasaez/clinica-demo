package es.demo.clinica.web;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** «Mis citas»: el paciente consulta y cancela su cita con el código de confirmación, sin cuenta. */
@Controller
public class PatientController {

    private final AppointmentService appointments;

    public PatientController(AppointmentService appointments) {
        this.appointments = appointments;
    }

    @GetMapping("/mis-citas")
    public String find(@RequestParam(required = false) String codigo, Model model) {
        model.addAttribute("codigo", codigo);
        if (codigo != null && !codigo.isBlank()) {
            try {
                model.addAttribute("appointment", appointments.findByCode(codigo));
            } catch (BookingException e) {
                model.addAttribute("bookingError", e.getMessage());
            }
        }
        return "mis-citas";
    }

    @PostMapping("/mis-citas/cancelar")
    public String cancel(@RequestParam String codigo, RedirectAttributes redirect) {
        try {
            appointments.cancelByCode(codigo);
            redirect.addFlashAttribute("message", "Tu cita se ha cancelado.");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/mis-citas?codigo=" + codigo.trim().toUpperCase();
    }
}
