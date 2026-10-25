package es.demo.clinica.messages;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findByAppointmentIdOrderByCreatedAtAsc(String appointmentId);
}
