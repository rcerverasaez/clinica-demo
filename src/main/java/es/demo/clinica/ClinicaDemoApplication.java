package es.demo.clinica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClinicaDemoApplication {

	private static final Logger log = LoggerFactory.getLogger(ClinicaDemoApplication.class);

	@Value("${server.port:8765}")
	private int puerto;

	public static void main(String[] args) {
		SpringApplication.run(ClinicaDemoApplication.class, args);
	}

	// Al terminar el arranque, muestra la dirección para abrir la aplicación en el navegador
	@EventListener(ApplicationReadyEvent.class)
	public void mostrarDireccion() {
		log.info("Clínica Demo lista. Ábrela en el navegador: http://localhost:{}", puerto);
	}

}
