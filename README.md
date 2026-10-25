# clinica-demo

Demo de una clínica para el curso de introducción a la IA. **Todos los datos son ficticios.** No usa datos ni marca de ninguna empresa real.

## Qué incluye

- Inicio con consultas y especialistas (`/`).
- Formulario de reserva para el paciente, público (`/reservar`).
- Vista de recepción con las citas próximas y cancelación (`/recepcion`). Requiere el rol RECEPCION.
- Zona de especialista (`/agenda`). Requiere el rol ESPECIALISTA. Se completa en la fase 2.
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

## API REST

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/treatments` | Público |
| GET | `/api/specialists` | Público |
| POST | `/api/appointments` | Público (422 si incumple las reglas) |
| GET | `/api/appointments` | RECEPCION |
| DELETE | `/api/appointments/{id}` | RECEPCION |

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
