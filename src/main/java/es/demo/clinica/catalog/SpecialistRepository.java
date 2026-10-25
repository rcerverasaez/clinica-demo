package es.demo.clinica.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistRepository extends JpaRepository<Specialist, String> {
}
