# clinica-demo

Demo de una clínica para el curso de introducción a la IA. **Todos los datos son ficticios.** No usa datos ni marca de ninguna empresa real.

## Qué incluye

- Inicio con consultas y especialistas (`/`).
- Formulario de reserva para el paciente (`/reservar`).
- Vista de recepción con las citas próximas y cancelación (`/recepcion`).
- Reglas de la clínica: horario de 9:00 a 18:00, de lunes a viernes, citas en punto o y media, sin solapes por especialista y solo en el futuro.

## Requisitos

- JDK 17 o superior.
- Maven 3.9 o superior (o el wrapper `./mvnw` que incluye el proyecto).

## Cómo se usa

Arrancar la app:

```bash
./mvnw spring-boot:run
```

Abrir http://localhost:8080 en el navegador.

Lanzar los tests:

```bash
./mvnw test
```

## Notas

- Las citas se guardan en memoria: se pierden al reiniciar la aplicación.
- No hay base de datos, autenticación ni envío real de correos. El correo solo se muestra en la confirmación.
- No hay variables de entorno ni secretos.
