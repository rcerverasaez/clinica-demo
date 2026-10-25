package es.demo.clinica.booking;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, String> {

    /** El especialista no atiende en algún momento de [start, end). */
    boolean existsBySpecialistIdAndStartAtLessThanAndEndAtGreaterThan(String specialistId,
            LocalDateTime end, LocalDateTime start);
}
