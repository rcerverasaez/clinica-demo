package es.demo.clinica.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Catálogo ficticio de la clínica demo. Los datos están en memoria. */
@Component
public class ClinicCatalog {

    private final List<Treatment> treatments = List.of(
            new Treatment("primera-visita", "Primera visita", 30, new BigDecimal("60.00")),
            new Treatment("revision", "Revisión anual", 45, new BigDecimal("90.00")),
            new Treatment("analitica", "Analítica completa", 20, new BigDecimal("45.00")),
            new Treatment("ecografia", "Ecografía", 40, new BigDecimal("120.00")));

    private final List<Specialist> specialists = List.of(
            new Specialist("ana-lopez", "Dra. Ana López", "Ginecología"),
            new Specialist("luis-garcia", "Dr. Luis García", "Medicina general"),
            new Specialist("marta-ruiz", "Dra. Marta Ruiz", "Endocrinología"));

    public List<Treatment> treatments() {
        return treatments;
    }

    public List<Specialist> specialists() {
        return specialists;
    }

    public Optional<Treatment> treatment(String id) {
        return treatments.stream().filter(t -> t.id().equals(id)).findFirst();
    }

    public Optional<Specialist> specialist(String id) {
        return specialists.stream().filter(s -> s.id().equals(id)).findFirst();
    }
}
