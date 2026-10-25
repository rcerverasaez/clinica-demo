package es.demo.clinica.booking;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;

/** Almacén en memoria de las citas. Se pierde al reiniciar la aplicación. */
@Repository
public class AppointmentRepository {

    private final List<Appointment> appointments = new CopyOnWriteArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public void remove(String id) {
        appointments.removeIf(a -> a.id().equals(id));
    }

    public List<Appointment> findAll() {
        return appointments.stream()
                .sorted(Comparator.comparing(Appointment::start))
                .toList();
    }
}
