package es.demo.clinica.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.demo.clinica.booking.BookingException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@Transactional
class ClinicCatalogTest {

    @Autowired
    private ClinicCatalog catalog;

    @Test
    void aSpecialistGetsAnIdFromItsNameWithoutAccents() {
        Specialist specialist = catalog.addSpecialist("Dra. Ana Núñez", "Dermatología");

        assertEquals("dra-ana-nunez", specialist.getId());
        assertTrue(catalog.specialist("dra-ana-nunez").isPresent());
    }

    @Test
    void aTreatmentKeepsItsPriceWithTwoDecimals() {
        Treatment treatment = catalog.addTreatment("Espirometría", 25, new BigDecimal("45"));

        assertEquals("45.00", treatment.getPrice().toPlainString());
    }

    @Test
    void aNameAlreadyInTheCatalogIsRejected() {
        BookingException error = assertThrows(BookingException.class,
                () -> catalog.addTreatment("Primera visita", 30, new BigDecimal("60")));
        assertEquals("catalog.duplicate", error.getMessage());
    }

    @Test
    void aDurationOutsideFiveToTwoHundredFortyMinutesIsRejected() {
        assertEquals("catalog.duration.invalid", errorOf(() -> catalog.addTreatment("Test", 4, BigDecimal.ONE)));
        assertEquals("catalog.duration.invalid", errorOf(() -> catalog.addTreatment("Test", 241, BigDecimal.ONE)));
    }

    @Test
    void aNegativePriceIsRejected() {
        assertEquals("catalog.price.invalid", errorOf(() -> catalog.addTreatment("Test", 30, new BigDecimal("-1"))));
    }

    @Test
    void aNameWithoutLettersOrNumbersIsRejected() {
        assertEquals("catalog.name.invalid", errorOf(() -> catalog.addSpecialist("!!!", "Medicina general")));
    }

    private static String errorOf(Runnable action) {
        return assertThrows(BookingException.class, action::run).getMessage();
    }
}
