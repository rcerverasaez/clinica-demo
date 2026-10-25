package es.demo.clinica.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    /** Hay una cita del especialista que se solapa con el intervalo [start, end). */
    boolean existsBySpecialistIdAndStartAtLessThanAndEndAtGreaterThan(String specialistId,
            LocalDateTime end, LocalDateTime start);

    List<Appointment> findByStartAtAfterOrderByStartAtAsc(LocalDateTime now);
}
