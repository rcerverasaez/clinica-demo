package es.demo.clinica.web;

import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.AppointmentStatus;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.catalog.ClinicCatalog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Agenda del día para especialistas: estado de cada cita y ausencias. */
@Controller
public class AgendaController {

    private final AppointmentService appointments;
    private final ClinicCatalog catalog;

    public AgendaController(AppointmentService appointments, ClinicCatalog catalog) {
        this.appointments = appointments;
        this.catalog = catalog;
    }

    @GetMapping("/agenda")
    public String day(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate day = date != null ? date : LocalDate.now();
        model.addAttribute("date", day);
        model.addAttribute("appointments", appointments.onDay(day));
        model.addAttribute("specialists", catalog.specialists());
        return "agenda";
    }

    @PostMapping("/agenda/{id}/estado")
    public String changeStatus(@PathVariable String id, @RequestParam AppointmentStatus status,
            @RequestParam LocalDate date, RedirectAttributes redirect) {
        redirect.addAttribute("date", date);
        try {
            appointments.changeStatus(id, status);
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/agenda";
    }

    @PostMapping("/agenda/ausencias")
    public String addAbsence(@RequestParam String specialistId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,
            @RequestParam String reason, RedirectAttributes redirect) {
        try {
            appointments.addAbsence(specialistId, start, end, reason);
            redirect.addFlashAttribute("message", "Ausencia registrada.");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/agenda";
    }
}
