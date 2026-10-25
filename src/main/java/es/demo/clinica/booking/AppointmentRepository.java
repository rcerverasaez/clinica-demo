package es.demo.clinica.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    /** Hay una cita confirmada del especialista que se solapa con [start, end). */
    boolean existsBySpecialistIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(String specialistId,
            AppointmentStatus status, LocalDateTime end, LocalDateTime start);

    List<Appointment> findByStatusAndStartAtAfterOrderByStartAtAsc(AppointmentStatus status, LocalDateTime now);

    List<Appointment> findByStartAtBetweenOrderByStartAtAsc(LocalDateTime from, LocalDateTime to);

    Optional<Appointment> findByCode(String code);
}
