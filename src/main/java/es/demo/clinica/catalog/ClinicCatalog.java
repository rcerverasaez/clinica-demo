package es.demo.clinica.catalog;

import es.demo.clinica.booking.BookingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo de consultas y especialistas: se consulta en la web y se amplía desde recepción. */
@Service
public class ClinicCatalog {

    private static final int MIN_DURATION = 5;
    private static final int MAX_DURATION = 240;
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MAX_TEXT = 120;
    private static final int MAX_ID = 60;

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

    /** Alta de especialista. El identificador sale del nombre, así que no puede repetirse. */
    @Transactional
    public Specialist addSpecialist(String name, String specialty) {
        String cleanName = requireText(name, "catalog.name.invalid");
        String cleanSpecialty = requireText(specialty, "catalog.specialty.invalid");
        String id = idFrom(cleanName);
        if (specialists.existsById(id)) {
            throw new BookingException("catalog.duplicate");
        }
        return specialists.save(new Specialist(id, cleanName, cleanSpecialty));
    }

    /** Alta de consulta, con duración entre 5 y 240 minutos y precio entre 0 y 999999,99. */
    @Transactional
    public Treatment addTreatment(String name, int durationMinutes, BigDecimal price) {
        String cleanName = requireText(name, "catalog.name.invalid");
        if (durationMinutes < MIN_DURATION || durationMinutes > MAX_DURATION) {
            throw new BookingException("catalog.duration.invalid");
        }
        if (price == null || price.signum() < 0 || price.compareTo(MAX_PRICE) > 0) {
            throw new BookingException("catalog.price.invalid");
        }
        String id = idFrom(cleanName);
        if (treatments.existsById(id)) {
            throw new BookingException("catalog.duplicate");
        }
        return treatments.save(new Treatment(id, cleanName, durationMinutes, price.setScale(2, RoundingMode.HALF_UP)));
    }

    private static String requireText(String value, String errorKey) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty() || text.length() > MAX_TEXT) {
            throw new BookingException(errorKey);
        }
        return text;
    }

    /** «Dra. Ana López» → «dra-ana-lopez». Sin letras ni números no hay identificador válido. */
    private static String idFrom(String name) {
        String ascii = Normalizer.normalize(name.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String id = ascii.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (id.isEmpty()) {
            throw new BookingException("catalog.name.invalid");
        }
        return id.length() > MAX_ID ? id.substring(0, MAX_ID).replaceAll("-+$", "") : id;
    }
}
