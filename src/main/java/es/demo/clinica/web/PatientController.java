package es.demo.clinica.web;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * «Mis citas»: el paciente consulta y cancela su cita con el código de confirmación, sin cuenta.
 * El código viaja en el cuerpo del formulario (POST), nunca en la URL.
 */
@Controller
public class PatientController {

    private final AppointmentService appointments;

    public PatientController(AppointmentService appointments) {
        this.appointments = appointments;
    }

    @GetMapping("/mis-citas")
    public String form() {
        return "mis-citas";
    }

    @PostMapping("/mis-citas")
    public String find(@RequestParam String codigo, Model model) {
        model.addAttribute("codigo", codigo);
        try {
            Appointment appointment = appointments.findByCode(codigo);
            model.addAttribute("appointment", appointment);
        } catch (BookingException e) {
            model.addAttribute("bookingError", e.getMessage());
        }
        return "mis-citas";
    }

    @PostMapping("/mis-citas/cancelar")
    public String cancel(@RequestParam String codigo, Model model) {
        try {
            appointments.cancelByCode(codigo);
            model.addAttribute("message", "Tu cita se ha cancelado.");
        } catch (BookingException e) {
            model.addAttribute("bookingError", e.getMessage());
        }
        return find(codigo, model);
    }
}
