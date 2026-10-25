package es.demo.clinica.booking;

import es.demo.clinica.catalog.ClinicCatalog;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Crea unas citas de ejemplo para los próximos días laborables. Se puede desactivar con clinica.demo-data=false. */
@Component
@ConditionalOnProperty(name = "clinica.demo-data", havingValue = "true", matchIfMissing = true)
class DemoData implements ApplicationRunner {

    private static final int APPOINTMENTS = 3;

    private final AppointmentService appointments;
    private final ClinicCatalog catalog;
    private final Clock clock;

    DemoData(AppointmentService appointments, ClinicCatalog catalog, Clock clock) {
        this.appointments = appointments;
        this.catalog = catalog;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        LocalDate day = LocalDate.now(clock);
        int booked = 0;
        while (booked < APPOINTMENTS) {
            day = day.plusDays(1);
            if (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            BookingForm form = new BookingForm();
            form.setTreatmentId(catalog.treatments().get(0).getId());
            form.setSpecialistId(catalog.specialists().get(0).getId());
            form.setPatientName("Paciente de ejemplo " + (booked + 1));
            form.setPatientEmail("paciente" + (booked + 1) + "@example.com");
            form.setStart(day.atTime(LocalTime.of(10, 0)));
            appointments.book(form);
            booked++;
        }
    }
}
