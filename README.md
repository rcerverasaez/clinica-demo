# clinica-demo

Demo de una clínica para el curso de introducción a la IA. **Todos los datos son ficticios.** No usa datos ni marca de ninguna empresa real.

## Qué incluye

- Inicio con consultas y especialistas (`/`).
- Reserva pública por huecos libres (`/reservar`): primero se elige consulta, especialista y día, y luego el hueco.
- «Mis citas» (`/mis-citas`): el paciente consulta o cancela su cita con el código de 8 caracteres que recibe al reservar. No necesita cuenta.
- Vista de recepción con las citas próximas, su código y cancelación (`/recepcion`). Requiere el rol RECEPCION.
- Agenda del día para el especialista (`/agenda`): marca cada cita como atendida o no presentada y registra ausencias. Requiere el rol ESPECIALISTA.
- Reglas de la clínica: horario de 9:00 a 18:00, de lunes a viernes, citas en punto o y media, sin solapes por especialista, sin ausencias del especialista y solo en el futuro.

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

## API REST

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/treatments` | Público |
| GET | `/api/specialists` | Público |
| POST | `/api/appointments` | Público (422 si incumple las reglas) |
| GET | `/api/appointments` | RECEPCION |
| DELETE | `/api/appointments/{id}` | RECEPCION (la cita pasa a CANCELADA) |

La documentación interactiva está en http://localhost:8080/swagger-ui.html y la especificación en `/v3/api-docs`.

## Accesos de demo

| Usuario | Contraseña por defecto | Rol |
|---|---|---|
| `recepcion` | `recepcion-demo` | RECEPCION |
| `especialista` | `especialista-demo` | ESPECIALISTA |

Son cuentas ficticias de demo. Para cambiarlas, define `CLINICA_RECEPCION_PASSWORD` y `CLINICA_ESPECIALISTA_PASSWORD` antes de arrancar.

## Notas

- La base de datos es H2 en memoria: los datos se pierden al reiniciar. El esquema lo crea Flyway (`src/main/resources/db/migration`).
- Las citas de ejemplo se crean al arrancar. Para desactivarlas, usa `clinica.demo-data=false`.
- No hay envío real de correos. El correo solo se muestra en la confirmación.
- Las citas canceladas no se borran: pasan a estado `CANCELADA` y el hueco vuelve a quedar libre.
- Estados de una cita: `CONFIRMADA`, `CANCELADA`, `ATENDIDA` y `NO_PRESENTADO`. La agenda solo cambia las confirmadas.
