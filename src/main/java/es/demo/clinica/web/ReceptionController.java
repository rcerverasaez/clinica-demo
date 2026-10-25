package es.demo.clinica.web;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.messages.MessageAuthor;
import es.demo.clinica.messages.MessageService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Vista de recepción: citas próximas, cancelación y conversación con el paciente. */
@Controller
public class ReceptionController {

    private final AppointmentService appointments;
    private final MessageService messages;
    private final AuditService audit;

    public ReceptionController(AppointmentService appointments, MessageService messages, AuditService audit) {
        this.appointments = appointments;
        this.messages = messages;
        this.audit = audit;
    }

    @GetMapping("/recepcion")
    public String list(Model model) {
        model.addAttribute("appointments", appointments.upcoming());
        return "recepcion";
    }

    @PostMapping("/recepcion/{id}/cancelar")
    public String cancel(@PathVariable String id, Principal principal) {
        appointments.cancel(id);
        audit.record(principal.getName(), AuditAction.CANCELLATION, id, "Cancelada desde recepción");
        return "redirect:/recepcion";
    }

    @GetMapping("/recepcion/citas/{id}")
    public String thread(@PathVariable String id, Model model, RedirectAttributes redirect) {
        try {
            Appointment appointment = appointments.get(id);
            model.addAttribute("appointment", appointment);
            model.addAttribute("messages", messages.forAppointment(id));
            return "recepcion-cita";
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
            return "redirect:/recepcion";
        }
    }

    @PostMapping("/recepcion/citas/{id}/mensajes")
    public String reply(@PathVariable String id, @RequestParam String body, Principal principal,
            RedirectAttributes redirect) {
        try {
            messages.send(id, MessageAuthor.RECEPCION, body);
            audit.record(principal.getName(), AuditAction.MESSAGE, id, "Respuesta de recepción");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/recepcion/citas/" + id;
    }
}
