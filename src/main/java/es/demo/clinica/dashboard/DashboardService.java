package es.demo.clinica.dashboard;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentRepository;
import es.demo.clinica.booking.AppointmentStatus;
import es.demo.clinica.catalog.ClinicCatalog;
import es.demo.clinica.catalog.Specialist;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cifras para recepción: citas por día, ocupación por especialista y no presentados. */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    static final int WEEK_DAYS = 7;
    static final int NO_SHOW_DAYS = 30;
    /** De 9:00 a 18:00 cada día laborable. */
    static final int WORKING_MINUTES = 8 * 60;

    private final AppointmentRepository appointments;
    private final ClinicCatalog catalog;
    private final Clock clock;

    public DashboardService(AppointmentRepository appointments, ClinicCatalog catalog, Clock clock) {
        this.appointments = appointments;
        this.catalog = catalog;
        this.clock = clock;
    }

    public Panel panel() {
        LocalDate today = LocalDate.now(clock);
        LocalDate end = today.plusDays(WEEK_DAYS);
        List<Appointment> week = active(appointments.findByStartAtBetweenOrderByStartAtAsc(
                today.atStartOfDay(), end.atStartOfDay()));

        var perDay = week.stream().collect(
                Collectors.groupingBy(a -> a.getStartAt().toLocalDate(), Collectors.counting()));
        List<DayCount> days = new ArrayList<>();
        for (int i = 0; i < WEEK_DAYS; i++) {
            LocalDate day = today.plusDays(i);
            days.add(new DayCount(day, perDay.getOrDefault(day, 0L)));
        }

        long workingDays = days.stream().filter(d -> isWorkingDay(d.getDay())).count();
        List<Occupancy> occupancy = new ArrayList<>();
        for (Specialist specialist : catalog.specialists()) {
            occupancy.add(new Occupancy(specialist.getName(), bookedMinutes(week, specialist),
                    (int) (workingDays * WORKING_MINUTES)));
        }

        long noShows = appointments.findByStartAtBetweenOrderByStartAtAsc(
                        today.minusDays(NO_SHOW_DAYS).atStartOfDay(), today.atStartOfDay()).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.NO_PRESENTADO)
                .count();

        return new Panel(days, occupancy, noShows);
    }

    private static List<Appointment> active(List<Appointment> list) {
        return list.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELADA).toList();
    }

    private static int bookedMinutes(List<Appointment> week, Specialist specialist) {
        return (int) week.stream()
                .filter(a -> a.getSpecialist().getId().equals(specialist.getId()))
                .mapToLong(a -> Duration.between(a.getStartAt(), a.getEndAt()).toMinutes())
                .sum();
    }

    private static boolean isWorkingDay(LocalDate day) {
        return day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    public static class Panel {

        private final List<DayCount> nextWeek;
        private final List<Occupancy> occupancy;
        private final long noShowsLast30Days;

        Panel(List<DayCount> nextWeek, List<Occupancy> occupancy, long noShowsLast30Days) {
            this.nextWeek = nextWeek;
            this.occupancy = occupancy;
            this.noShowsLast30Days = noShowsLast30Days;
        }

        public List<DayCount> getNextWeek() {
            return nextWeek;
        }

        public List<Occupancy> getOccupancy() {
            return occupancy;
        }

        public long getNoShowsLast30Days() {
            return noShowsLast30Days;
        }
    }

    public static class DayCount {

        private final LocalDate day;
        private final long appointments;

        DayCount(LocalDate day, long appointments) {
            this.day = day;
            this.appointments = appointments;
        }

        public LocalDate getDay() {
            return day;
        }

        public long getAppointments() {
            return appointments;
        }
    }

    public static class Occupancy {

        private final String specialist;
        private final int bookedMinutes;
        private final int availableMinutes;

        Occupancy(String specialist, int bookedMinutes, int availableMinutes) {
            this.specialist = specialist;
            this.bookedMinutes = bookedMinutes;
            this.availableMinutes = availableMinutes;
        }

        public String getSpecialist() {
            return specialist;
        }

        public int getPercent() {
            return availableMinutes == 0 ? 0 : bookedMinutes * 100 / availableMinutes;
        }
    }
}
