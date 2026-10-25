package es.demo.clinica.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import es.demo.clinica.FixedClockTestConfig;
import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentRepository;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.AppointmentStatus;
import es.demo.clinica.booking.BookingForm;
import es.demo.clinica.catalog.ClinicCatalog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@Transactional
@Import(FixedClockTestConfig.class)
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboard;

    @Autowired
    private AppointmentService appointments;

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private ClinicCatalog catalog;

    @Test
    void countsAppointmentsPerDayAndBookedMinutesPerSpecialist() {
        book("2030-01-07T10:00");
        book("2030-01-07T11:00");

        DashboardService.Panel panel = dashboard.panel();

        assertEquals(LocalDate.of(2030, 1, 7), panel.getNextWeek().get(0).getDay());
        assertEquals(2, panel.getNextWeek().get(0).getAppointments());
        DashboardService.Occupancy ana = panel.getOccupancy().stream()
                .filter(o -> o.getSpecialist().contains("López"))
                .findFirst().orElseThrow();
        assertEquals(60 * 100 / (5 * 9 * 60), ana.getPercent());
    }

    @Test
    void countsNoShowsOfTheLastMonth() {
        Appointment past = new Appointment(UUID.randomUUID().toString(), "PASADA01",
                catalog.treatment("primera-visita").orElseThrow(),
                catalog.specialist("ana-lopez").orElseThrow(),
                "Paciente de prueba", "paciente@example.com", LocalDateTime.parse("2029-12-20T10:00"));
        past.changeStatus(AppointmentStatus.NO_PRESENTADO);
        repository.save(past);

        assertEquals(1, dashboard.panel().getNoShowsLast30Days());
    }

    private void book(String start) {
        BookingForm form = new BookingForm();
        form.setTreatmentId("primera-visita");
        form.setSpecialistId("ana-lopez");
        form.setPatientName("Paciente de prueba");
        form.setPatientEmail("paciente@example.com");
        form.setStart(LocalDateTime.parse(start));
        appointments.book(form);
    }
}
