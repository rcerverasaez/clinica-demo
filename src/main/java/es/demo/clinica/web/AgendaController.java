package es.demo.clinica.web;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.AppointmentStatus;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.documents.DocumentService;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Agenda del día para especialistas: estado de cada cita, documentos y ausencias. */
@Controller
public class AgendaController {

    private final AppointmentService appointments;
    private final ClinicCatalog catalog;
    private final DocumentService documents;
    private final AuditService audit;

    public AgendaController(AppointmentService appointments, ClinicCatalog catalog, DocumentService documents,
            AuditService audit) {
        this.appointments = appointments;
        this.catalog = catalog;
        this.documents = documents;
        this.audit = audit;
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
            @RequestParam LocalDate date, Principal principal, RedirectAttributes redirect) {
        redirect.addAttribute("date", date);
        try {
            appointments.changeStatus(id, status);
            audit.record(principal.getName(), AuditAction.STATUS_CHANGE, id, "audit.detail.status." + status.name());
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/agenda";
    }

    @PostMapping("/agenda/ausencias")
    public String addAbsence(@RequestParam String specialistId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,
            @RequestParam String reason, Principal principal, RedirectAttributes redirect) {
        try {
            appointments.addAbsence(specialistId, start, end, reason);
            audit.record(principal.getName(), AuditAction.ABSENCE, specialistId, "audit.detail.absence");
            redirect.addFlashAttribute("message", "message.absence-saved");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/agenda";
    }

    @GetMapping("/agenda/citas/{id}")
    public String appointment(@PathVariable String id, Model model, RedirectAttributes redirect) {
        try {
            Appointment appointment = appointments.get(id);
            model.addAttribute("appointment", appointment);
            model.addAttribute("documents", documents.forAppointment(id));
            return "agenda-cita";
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
            return "redirect:/agenda";
        }
    }

    @PostMapping("/agenda/citas/{id}/documentos")
    public String upload(@PathVariable String id, @RequestParam("file") MultipartFile file, Principal principal,
            RedirectAttributes redirect) {
        try {
            documents.upload(id, file.getOriginalFilename(), file.getContentType(), file.getBytes(),
                    principal.getName());
            audit.record(principal.getName(), AuditAction.DOCUMENT_UPLOAD, id, "audit.detail.document");
            redirect.addFlashAttribute("message", "message.document-saved");
        } catch (BookingException e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        } catch (IOException e) {
            redirect.addFlashAttribute("bookingError", "document.read-error");
        }
        return "redirect:/agenda/citas/" + id;
    }
}
