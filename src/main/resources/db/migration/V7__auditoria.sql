-- Registro de acciones. El detalle nunca lleva datos personales: nombres, correos o códigos de cita.
CREATE TABLE audit_events (
    id VARCHAR(36) PRIMARY KEY,
    occurred_at TIMESTAMP NOT NULL,
    actor VARCHAR(60) NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    target VARCHAR(60),
    detail VARCHAR(200) NOT NULL
);

CREATE INDEX idx_audit_occurred ON audit_events (occurred_at);
