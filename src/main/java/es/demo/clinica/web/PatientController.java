package es.demo.clinica.web;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import es.demo.clinica.documents.Document;
import es.demo.clinica.documents.DocumentService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * «Mis citas»: el paciente consulta, cancela y descarga documentos con el código de confirmación, sin cuenta.
 * El código viaja en el cuerpo del formulario (POST), nunca en la URL.
 */
@Controller
public class PatientController {

    private final AppointmentService appointments;
    private final DocumentService documents;

    public PatientController(AppointmentService appointments, DocumentService documents) {
        this.appointments = appointments;
        this.documents = documents;
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
            model.addAttribute("documents", documents.forAppointment(appointment.getId()));
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

    @PostMapping("/mis-citas/documentos/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id, @RequestParam String codigo) {
        try {
            Document document = documents.downloadForCode(id, codigo);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename(document.getFileName()).build().toString())
                    .body(document.getContent());
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }
}
