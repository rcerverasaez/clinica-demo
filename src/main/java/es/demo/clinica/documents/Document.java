package es.demo.clinica.documents;

import es.demo.clinica.booking.Appointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** Archivo adjunto a una cita. El contenido se guarda en la base de datos: es una demo sin almacenamiento externo. */
@Entity
@Table(name = "documents")
public class Document {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    private byte[] content;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    protected Document() {
    }

    public Document(Appointment appointment, String fileName, String contentType, byte[] content,
            String uploadedBy, LocalDateTime uploadedAt) {
        this.id = UUID.randomUUID().toString();
        this.appointment = appointment;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public String getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
