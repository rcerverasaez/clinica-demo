package es.demo.clinica;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Reloj de la clínica: horario de Madrid. Los tests lo sustituyen por uno fijo. */
@Configuration
class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Europe/Madrid"));
    }
}
