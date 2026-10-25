-- Catálogo ficticio de la clínica demo.
INSERT INTO treatments (id, name, duration_minutes, price) VALUES
    ('primera-visita', 'Primera visita', 30, 60.00),
    ('revision', 'Revisión anual', 45, 90.00),
    ('analitica', 'Analítica completa', 20, 45.00),
    ('ecografia', 'Ecografía', 40, 120.00);

INSERT INTO specialists (id, name, specialty) VALUES
    ('ana-lopez', 'Dra. Ana López', 'Ginecología'),
    ('luis-garcia', 'Dr. Luis García', 'Medicina general'),
    ('marta-ruiz', 'Dra. Marta Ruiz', 'Endocrinología');
