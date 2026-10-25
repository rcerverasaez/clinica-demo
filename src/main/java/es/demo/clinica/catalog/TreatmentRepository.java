package es.demo.clinica.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, String> {
}
