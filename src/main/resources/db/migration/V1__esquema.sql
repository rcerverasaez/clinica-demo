-- Esquema inicial de la clínica demo (datos ficticios).
CREATE TABLE treatments (
    id VARCHAR(60) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    duration_minutes INT NOT NULL,
    price DECIMAL(8, 2) NOT NULL
);

CREATE TABLE specialists (
    id VARCHAR(60) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    specialty VARCHAR(120) NOT NULL
);

CREATE TABLE appointments (
    id VARCHAR(36) PRIMARY KEY,
    treatment_id VARCHAR(60) NOT NULL REFERENCES treatments (id),
    specialist_id VARCHAR(60) NOT NULL REFERENCES specialists (id),
    patient_name VARCHAR(80) NOT NULL,
    patient_email VARCHAR(120) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_appointments_specialist_start ON appointments (specialist_id, start_at);
