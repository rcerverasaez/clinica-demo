package es.demo.clinica.notifications;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    boolean existsByAppointmentIdAndKind(String appointmentId, String kind);

    List<Notification> findAllByOrderBySentAtDesc();
}
