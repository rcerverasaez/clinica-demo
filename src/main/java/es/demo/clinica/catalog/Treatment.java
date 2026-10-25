package es.demo.clinica.catalog;

import java.math.BigDecimal;

/** Consulta o prueba que el paciente puede reservar. */
public record Treatment(String id, String name, int durationMinutes, BigDecimal price) {
}
