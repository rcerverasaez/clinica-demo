-- Estado de cada cita y código para que el paciente la consulte sin cuenta.
ALTER TABLE appointments ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA';
ALTER TABLE appointments ADD COLUMN code VARCHAR(8);
CREATE UNIQUE INDEX idx_appointments_code ON appointments (code);

-- Días o franjas en que un especialista no atiende (vacaciones, congresos, etc.).
CREATE TABLE absences (
    id VARCHAR(36) PRIMARY KEY,
    specialist_id VARCHAR(60) NOT NULL REFERENCES specialists (id),
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    reason VARCHAR(120) NOT NULL
);
