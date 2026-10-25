package es.demo.clinica.documents;

import es.demo.clinica.booking.Appointment;
import es.demo.clinica.booking.AppointmentService;
import es.demo.clinica.booking.BookingException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Documentos de la cita: los sube el especialista y los descarga el paciente con el código. */
@Service
@Transactional
public class DocumentService {

    static final long MAX_BYTES = 2L * 1024 * 1024;
    private static final Set<String> TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
    private static final String NOT_FOUND = "document.notfound";

    private final DocumentRepository documents;
    private final AppointmentService appointments;
    private final Clock clock;

    public DocumentService(DocumentRepository documents, AppointmentService appointments, Clock clock) {
        this.documents = documents;
        this.appointments = appointments;
        this.clock = clock;
    }

    public Document upload(String appointmentId, String fileName, String contentType, byte[] content,
            String uploadedBy) {
        Appointment appointment = appointments.get(appointmentId);
        if (content.length == 0) {
            throw new BookingException("document.empty");
        }
        if (content.length > MAX_BYTES) {
            throw new BookingException("document.too-large");
        }
        if (!TYPES.contains(contentType)) {
            throw new BookingException("document.type");
        }
        return documents.save(new Document(appointment, cleanName(fileName), contentType, content, uploadedBy,
                LocalDateTime.now(clock)));
    }

    @Transactional(readOnly = true)
    public List<Document> forAppointment(String appointmentId) {
        return documents.findByAppointmentIdOrderByUploadedAtAsc(appointmentId);
    }

    /** Solo se descarga un documento con el código de la cita a la que pertenece. */
    @Transactional(readOnly = true)
    public Document downloadForCode(String documentId, String code) {
        Document document = documents.findById(documentId)
                .orElseThrow(() -> new BookingException(NOT_FOUND));
        Appointment owner = appointments.findByCode(code);
        if (!document.getAppointment().getId().equals(documentId)) {
            throw new BookingException(NOT_FOUND);
        }
        return document;
    }

    private static String cleanName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "documento";
        }
        String name = fileName.replaceAll("[\\\\/]", "_").trim();
        return name.length() > 120 ? name.substring(0, 120) : name;
    }
}
