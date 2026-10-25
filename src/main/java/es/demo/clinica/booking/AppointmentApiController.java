package es.demo.clinica.booking;

import es.demo.clinica.audit.AuditAction;
import es.demo.clinica.audit.AuditService;
import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.catalog.Specialist;
import es.demo.clinica.catalog.Treatment;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** API REST de la clínica: catálogo y citas. Pensada para una app móvil o para otros sistemas. */
@RestController
@Tag(name = "Citas", description = "Catálogo y reservas de la clínica")
public class AppointmentApiController {

    private final ClinicCatalog catalog;
    private final AppointmentService appointments;
    private final AuditService audit;

    public AppointmentApiController(ClinicCatalog catalog, AppointmentService appointments, AuditService audit) {
        this.catalog = catalog;
        this.appointments = appointments;
        this.audit = audit;
    }

    @GetMapping("/api/treatments")
    @Operation(summary = "Lista las consultas que se pueden reservar")
    public List<Treatment> treatments() {
        return catalog.treatments();
    }

    @GetMapping("/api/specialists")
    @Operation(summary = "Lista los especialistas")
    public List<Specialist> specialists() {
        return catalog.specialists();
    }

    @PostMapping("/api/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reserva una cita", description = "Devuelve 422 si la reserva incumple las reglas de la clínica.")
    public Appointment book(@Valid @RequestBody BookingForm form) {
        Appointment appointment = appointments.book(form);
        audit.record(AuditService.API_ACTOR, AuditAction.BOOKING, appointment.getId(), "Reserva por API");
        return appointment;
    }

    @GetMapping("/api/appointments")
    @Operation(summary = "Lista las citas próximas (solo recepción)")
    public List<Appointment> upcoming() {
        return appointments.upcoming();
    }

    @DeleteMapping("/api/appointments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancela una cita (solo recepción)")
    public void cancel(@PathVariable String id, Principal principal) {
        appointments.cancel(id);
        audit.record(principal.getName(), AuditAction.CANCELLATION, id, "Cancelada por API");
    }
}
