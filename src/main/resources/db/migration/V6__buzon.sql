-- Buzón de salida: los recordatorios se guardan aquí en lugar de enviarse por correo.
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY,
    appointment_id VARCHAR(36) NOT NULL REFERENCES appointments (id),
    kind VARCHAR(30) NOT NULL,
    recipient VARCHAR(120) NOT NULL,
    subject VARCHAR(120) NOT NULL,
    body VARCHAR(500) NOT NULL,
    sent_at TIMESTAMP NOT NULL
);

-- Cada cita recibe como mucho un recordatorio.
CREATE UNIQUE INDEX idx_notifications_reminder ON notifications (appointment_id, kind);
