package es.demo.clinica;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Reloj fijo para que las pruebas de fechas no dependan del día en que se ejecuten. */
@TestConfiguration
public class FixedClockTestConfig {

    // Lunes 7 de enero de 2030, 8:00.
    @Bean
    @Primary
    Clock fixedClock() {
        ZoneId zone = ZoneId.of("Europe/Madrid");
        return Clock.fixed(LocalDateTime.of(2030, 1, 7, 8, 0).atZone(zone).toInstant(), zone);
    }
}
