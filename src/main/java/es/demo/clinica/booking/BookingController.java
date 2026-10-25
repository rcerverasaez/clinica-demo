package es.demo.clinica.booking;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.catalog.ClinicCatalog;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Formulario de reserva para el paciente, con huecos libres por día. */
@Controller
public class BookingController {

    private final AppointmentService appointments;
    private final ClinicCatalog catalog;
    private final AuditService audit;

    public BookingController(AppointmentService appointments, ClinicCatalog catalog, AuditService audit) {
        this.appointments = appointments;
        this.catalog = catalog;
        this.audit = audit;
    }

    @GetMapping("/reservar")
    public String showForm(@RequestParam(required = false) String treatmentId,
            @RequestParam(required = false) String specialistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
            Model model) {
        BookingForm form = new BookingForm();
        form.setTreatmentId(treatmentId);
        form.setSpecialistId(specialistId);
        form.setStart(start);
        model.addAttribute("form", form);
        model.addAttribute("date", date);
        if (treatmentId != null && specialistId != null && date != null) {
            List<LocalDateTime> slots = appointments.availableSlots(specialistId, treatmentId, date);
            model.addAttribute("slots", slots);
        }
        addCatalog(model);
        return "reservar";
    }

    @PostMapping("/reservar")
    public String submit(@Valid @ModelAttribute("form") BookingForm form, BindingResult errors,
            Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            addCatalog(model);
            return "reservar";
        }
        try {
            Appointment appointment = appointments.book(form);
            audit.record(AuditService.PUBLIC_ACTOR, AuditAction.BOOKING, appointment.getId(), "Reserva desde la web");
            redirect.addFlashAttribute("confirmed", appointment);
            return "redirect:/reservar";
        } catch (BookingException e) {
            model.addAttribute("bookingError", e.getMessage());
            addCatalog(model);
            return "reservar";
        }
    }

    private void addCatalog(Model model) {
        model.addAttribute("treatments", catalog.treatments());
        model.addAttribute("specialists", catalog.specialists());
    }
}
