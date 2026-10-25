package es.demo.clinica.messages;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Conversación entre paciente y recepción sobre una cita. */
@Service
@Transactional
public class MessageService {

    static final int MAX_LENGTH = 300;

    private final MessageRepository messages;
    private final AppointmentService appointments;
    private final Clock clock;

    public MessageService(MessageRepository messages, AppointmentService appointments, Clock clock) {
        this.messages = messages;
        this.appointments = appointments;
        this.clock = clock;
    }

    public Message send(String appointmentId, MessageAuthor author, String body) {
        Appointment appointment = appointments.get(appointmentId);
        String text = body == null ? "" : body.trim();
        if (text.isEmpty()) {
            throw new BookingException("message.empty");
        }
        if (text.length() > MAX_LENGTH) {
            throw new BookingException("message.too-long");
        }
        return messages.save(new Message(appointment, author, text, LocalDateTime.now(clock)));
    }

    @Transactional(readOnly = true)
    public List<Message> forAppointment(String appointmentId) {
        return messages.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);
    }
}
