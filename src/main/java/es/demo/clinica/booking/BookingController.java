package es.demo.clinica.booking;

import es.demo.clinica.catalog.ClinicCatalog;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Formulario de reserva para el paciente. */
@Controller
public class BookingController {

    private final AppointmentService appointments;
    private final ClinicCatalog catalog;

    public BookingController(AppointmentService appointments, ClinicCatalog catalog) {
        this.appointments = appointments;
        this.catalog = catalog;
    }

    @GetMapping("/reservar")
    public String showForm(Model model) {
        model.addAttribute("form", new BookingForm());
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
