# clinica-demo

Demo de una clínica para el curso de introducción a la IA. **Todos los datos son ficticios.** No usa datos ni marca de ninguna empresa real.

**Material didáctico:** el proyecto contiene errores y situaciones preparados para las prácticas. No está destinado a producción ni debe utilizarse con pacientes o datos reales. Las cuentas, contraseñas y claves de ejemplo son ficticias.

## Descargar la versión del curso

La versión de partida es **`curso-ivi-v1.0.0`**. No necesitas una cuenta de GitHub para descargar este repositorio público.

### Con Git

```bash
git clone --branch curso-ivi-v1.0.0 --depth 1 https://github.com/rcerverasaez/clinica-demo.git
cd clinica-demo
```

Así obtienes la versión concreta del curso, aunque el proyecto cambie más adelante. Git puede avisar de que estás en `detached HEAD` porque has descargado una etiqueta: puedes modificar archivos y revisar el diff con normalidad.

### Como ZIP

[Descargar clinica-demo — versión del curso](https://github.com/rcerverasaez/clinica-demo/releases/download/curso-ivi-v1.0.0/clinica-demo-curso-ivi-v1.0.0.zip).

Descomprime el archivo y abre la carpeta `clinica-demo` en Cursor o VS Code. El ZIP incluye el mismo código y las mismas instrucciones que la versión de Git, pero no incluye el historial.

Para revisar cambios con Git en la copia del ZIP, crea un punto de partida antes de editar:

```bash
cd clinica-demo
git init
git add .
git commit -m "Punto de partida del curso"
```

Si Git solicita tu nombre y correo, configúralos en esa carpeta antes de repetir el commit. No necesitas subir tus cambios a GitHub.

### Preparar las dependencias

Con JDK 17 o superior instalado, ejecuta desde la carpeta del proyecto, antes de la sesión:

```bash
./mvnw -B dependency:go-offline
```

En Windows (PowerShell o CMD), utiliza `mvnw.cmd` en lugar de `./mvnw`. La primera descarga requiere conexión a internet. El proyecto incluye el wrapper de Maven: no necesitas instalar Maven por separado.


## Qué incluye

- Inicio con consultas y especialistas (`/`).
- Reserva pública por huecos libres (`/reservar`): primero se elige consulta, especialista y día, y luego el hueco.
- «Mis citas» (`/mis-citas`): el paciente consulta o cancela su cita, descarga los documentos de su cita y escribe a recepción. Lo hace con el código de 8 caracteres que recibe al reservar. No necesita cuenta.
- Recepción (`/recepcion`, rol RECEPCION): citas próximas con su código, cancelación y conversación con cada paciente. Desde ahí se llega al panel (`/recepcion/panel`), al buzón de recordatorios (`/recepcion/buzon`), al registro de acciones (`/recepcion/auditoria`) y al catálogo (`/recepcion/catalogo`), donde se añaden especialistas y consultas.
- Agenda del día para el especialista (`/agenda`, rol ESPECIALISTA): marca cada cita como atendida o no presentada, registra ausencias y sube documentos (PDF, JPG o PNG de hasta 2 MB) a cada cita.
- Reglas de la clínica: horario de 9:00 a 18:00, de lunes a viernes, citas en punto o y media, sin solapes por especialista, sin ausencias del especialista y solo en el futuro.
- Accesible: enlace «Ir al contenido», avisos que leen los lectores de pantalla y un menú que se adapta al móvil.

## Requisitos

- JDK 17 o superior. Comprueba la versión con `java -version`. Si tienes varios JDK instalados, apunta `JAVA_HOME` a uno de 17 o más antes de lanzar Maven.
- No hace falta instalar Maven: `./mvnw` descarga la versión que usa el proyecto la primera vez.
- Opcional: Docker, si quieres arrancar la app con la sección «Con Docker» en vez de con Maven.

## Cómo se usa

Arrancar la app:

```bash
./mvnw spring-boot:run
```

Abrir http://localhost:8080 en el navegador. La interfaz arranca en español; para verla en inglés, añade `?lang=en` a la dirección o pulsa el enlace *English* de la cabecera (`?lang=es` vuelve al español).

Lanzar los tests:

```bash
./mvnw test
```

El estado de partida contiene fallos preparados para las prácticas. Algunas pruebas pueden fallar antes de que hagas cambios: registra el resultado inicial para distinguir los fallos previos de los nuevos.

## Con Docker

Si tienes Docker en marcha, puedes arrancar la app sin tener Java instalado:

```bash
docker compose up --build
```

Abre http://localhost:8080. Para cambiar las contraseñas de demo, copia `.env.example` a `.env` y edita los valores. El fichero `.env` no se sube al repositorio.

## Si algo falla

Es normal que algo falle al instalar: cada equipo tiene su propia versión de Java, su red y su configuración. Antes de pedir ayuda, mira si tu error aparece en esta tabla:

| Síntoma | Causa probable | Qué hacer |
|---|---|---|
| `class file has wrong version 61.0, should be 52.0` o `UnsupportedClassVersionError` | Maven está usando un Java anterior a 17 | Comprueba `java -version`, apunta `JAVA_HOME` a un JDK 17 o superior y vuelve a lanzar el comando. |
| `Port 8080 was already in use` | Otro programa está usando el puerto 8080 | Cierra ese programa o arranca con `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081` y abre http://localhost:8081. |
| `Cannot connect to the Docker daemon` | Docker no está en marcha | Abre Docker Desktop (o arranca el servicio de Docker) y vuelve a lanzar `docker compose up --build`. |
| `Could not resolve dependencies` o `Could not transfer artifact` | No hay conexión a internet, o la red bloquea las descargas | Comprueba la conexión y vuelve a lanzar el comando. En una red de empresa, pregunta a quien administra la red. |
| Un test falla justo después de cambiar algo | El cambio rompe una regla de la clínica | Lee el nombre del test y el mensaje completo: el propio test dice qué se esperaba. |

### Cómo pedirle ayuda a una IA

Una IA ayuda mucho más si le das contexto. Pega el error completo, no un resumen, y explica qué estabas haciendo:

```text
Estoy instalando clinica-demo siguiendo la sección "Cómo se usa" del README.
Sistema operativo: (Windows, macOS o Linux)
Comando que ejecuto: ./mvnw spring-boot:run
Resultado de java -version: (pégalo aquí)
Error completo:
(pega aquí todo lo que aparece en la terminal)

¿Qué causa este error? Explícamelo antes de darme comandos y no cambies
archivos del proyecto sin preguntarme.
```

Unas reglas sencillas:

- Pide que te **expliquen la causa**, no solo que lo arreglen. Así puedes comprobar la respuesta y aprendes para la próxima vez.
- Ejecuta **un comando cada vez** y pega de vuelta el resultado.
- Antes de pegar un error, quita contraseñas, tokens y correos reales. Las contraseñas de demo de este README son ficticias, pero las tuyas no deben salir de tu equipo.
- Si la IA propone borrar carpetas, editar `pom.xml` o instalar algo, pregunta por qué antes de hacerlo.
- La IA también se equivoca. Si una respuesta no encaja con lo que ves, vuelve a preguntar con el resultado nuevo.

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

## Integración continua

GitHub Actions ejecuta `./mvnw verify` en cada push y en cada pull request (`.github/workflows/ci.yml`). Si una ejecución sale en rojo, abre el registro y busca el primer test que falla: el propio test dice qué se esperaba.

## Notas

- La base de datos es H2 en memoria: los datos se pierden al reiniciar. El esquema lo crea Flyway (`src/main/resources/db/migration`).
- Las citas de ejemplo se crean al arrancar. Para desactivarlas, usa `clinica.demo-data=false`.
- No se envía ningún correo de verdad. Los recordatorios se preparan cada hora y se guardan en el buzón de recepción; la confirmación de una reserva se muestra en pantalla.
- El registro de acciones no guarda nombres, correos ni códigos de cita.
- Las citas canceladas no se borran: pasan a estado `CANCELADA` y el hueco vuelve a quedar libre.
- Estados de una cita: `CONFIRMADA`, `CANCELADA`, `ATENDIDA` y `NO_PRESENTADO`. La agenda solo cambia las confirmadas.
