package es.demo.clinica.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Consulta o prueba que el paciente puede reservar. */
@Entity
@Table(name = "treatments")
public class Treatment {

    @Id
    private String id;

    private String name;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    private BigDecimal price;

    protected Treatment() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
