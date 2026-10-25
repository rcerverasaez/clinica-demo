package es.demo.clinica.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/** Consulta el catálogo de consultas y especialistas. */
@Service
public class ClinicCatalog {

    private final TreatmentRepository treatments;
    private final SpecialistRepository specialists;

    public ClinicCatalog(TreatmentRepository treatments, SpecialistRepository specialists) {
        this.treatments = treatments;
        this.specialists = specialists;
    }

    public List<Treatment> treatments() {
        return treatments.findAll(Sort.by("name"));
    }

    public List<Specialist> specialists() {
        return specialists.findAll(Sort.by("name"));
    }

    public Optional<Treatment> treatment(String id) {
        return treatments.findById(id);
    }

    public Optional<Specialist> specialist(String id) {
        return specialists.findById(id);
    }
}
