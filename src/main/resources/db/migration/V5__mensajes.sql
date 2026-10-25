-- Conversación entre el paciente y recepción, ligada a la cita.
CREATE TABLE messages (
    id VARCHAR(36) PRIMARY KEY,
    appointment_id VARCHAR(36) NOT NULL REFERENCES appointments (id),
    author VARCHAR(20) NOT NULL,
    body VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_messages_appointment ON messages (appointment_id);
