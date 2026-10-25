-- Documentos que el especialista adjunta a una cita. El paciente los descarga con el código de la cita.
CREATE TABLE documents (
    id VARCHAR(36) PRIMARY KEY,
    appointment_id VARCHAR(36) NOT NULL REFERENCES appointments (id),
    file_name VARCHAR(120) NOT NULL,
    content_type VARCHAR(60) NOT NULL,
    content BLOB NOT NULL,
    uploaded_by VARCHAR(60) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_documents_appointment ON documents (appointment_id);
