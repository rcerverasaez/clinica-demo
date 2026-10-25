package es.demo.clinica.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Profesional que atiende las citas. */
@Entity
@Table(name = "specialists")
public class Specialist {

    @Id
    private String id;

    private String name;

    private String specialty;

    protected Specialist() {
    }

    public Specialist(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }
}
