# clinica-demo

Demo de una clínica para el curso de introducción a la IA. **Todos los datos son ficticios.** No usa datos ni marca de ninguna empresa real.

**Material didáctico:** el proyecto contiene errores y situaciones preparados para las prácticas. No está destinado a producción ni debe utilizarse con pacientes o datos reales. Las cuentas, contraseñas y claves de ejemplo son ficticias.

## Tu copia para el curso

La versión de partida es **`curso-ivi-v1.0.0`**. Para las prácticas y para subir tus cambios no necesitas cuenta de GitHub: el ponente te dará un **token**, una clave de acceso que solo sirve para este repositorio de prueba. Es independiente de la cuenta de ChatGPT Free que utilizarás con Codex.

### Descargar el proyecto

Necesitas Git instalado. En Cursor o VS Code, abre una terminal (**Terminal → New Terminal**) y ejecuta:

```bash
# Ve a tu carpeta de Descargas
cd ~/Downloads

# Descarga el proyecto, cambiando TOKEN por el que has recibido
git clone https://alumno:TOKEN@github.com/rcerverasaez/clinica-demo.git

# Entra en la carpeta del proyecto
cd clinica-demo

# Escribe tu nombre, que aparecerá en tus cambios. El correo puede ser inventado
git config user.name "Tu nombre"
git config user.email "tu-nombre@example.com"
```

Usa el token solo para este comando: no lo pegues en el chat de Codex ni lo publiques. Después abre la carpeta `Descargas/clinica-demo` en el editor (**File → Open Folder**).

### Subir tus cambios

Es un proyecto de prueba: puedes modificar lo que quieras. Para las prácticas, utiliza una rama con tu nombre, porque todos corregiréis los mismos fallos y así evitaréis conflictos:

```bash
git switch -c tu-nombre
```

Si `main` ya ha cambiado y quieres partir de la versión original, usa `git switch -c tu-nombre curso-ivi-v1.0.0`.

Cuando hayas revisado un cambio, guárdalo y súbelo:

```bash
git diff
git add ruta/del/archivo
git commit -m "Describe el cambio realizado"
git push -u origin tu-nombre
```

Sustituye `tu-nombre` por el nombre de tu rama y `ruta/del/archivo` por cada archivo que hayas revisado. No hace falta iniciar sesión: el token del clon ya da permiso para subir. También puedes pedírselo a Codex, que te pedirá aprobar cada comando de Git con «Permitir una vez».

### ZIP como alternativa de descarga

[Descargar clinica-demo — versión del curso](https://github.com/rcerverasaez/clinica-demo/releases/download/curso-ivi-v1.0.0/clinica-demo-curso-ivi-v1.0.0.zip).

Descomprime el archivo y abre la carpeta `clinica-demo` en el editor. El ZIP contiene el código de la versión del curso y permite seguir la demostración, pero no incluye historial ni un remoto al que subir cambios. Para las prácticas de commit y push, utiliza la clonación indicada arriba.

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

Abrir http://localhost:8765 en el navegador. La interfaz arranca en español; para verla en inglés, añade `?lang=en` a la dirección o pulsa el enlace *English* de la cabecera (`?lang=es` vuelve al español).

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

Abre http://localhost:8765. Para cambiar las contraseñas de demo, copia `.env.example` a `.env` y edita los valores. El fichero `.env` no se sube al repositorio.

## Si algo falla

Es normal que algo falle al instalar: cada equipo tiene su propia versión de Java, su red y su configuración. Antes de pedir ayuda, mira si tu error aparece en esta tabla:

| Síntoma | Causa probable | Qué hacer |
|---|---|---|
| `class file has wrong version 61.0, should be 52.0` o `UnsupportedClassVersionError` | Maven está usando un Java anterior a 17 | Comprueba `java -version`, apunta `JAVA_HOME` a un JDK 17 o superior y vuelve a lanzar el comando. |
| `Port 8765 was already in use` | Otro programa está usando el puerto 8765 | Cierra ese programa o arranca con `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8766` y abre http://localhost:8766. |
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

La documentación interactiva está en http://localhost:8765/swagger-ui.html y la especificación en `/v3/api-docs`.

## Accesos de demo

| Usuario | Contraseña por defecto | Rol |
|---|---|---|
| `recepcion` | `recepcion-demo` | RECEPCION |
| `especialista` | `especialista-demo` | ESPECIALISTA |
| `vbote@vbote.com` | `Hola.001` | RECEPCION y ESPECIALISTA |

Son cuentas ficticias de demo. Para cambiarlas, define `CLINICA_RECEPCION_PASSWORD` y `CLINICA_ESPECIALISTA_PASSWORD` y `CLINICA_VBOTE_PASSWORD` antes de arrancar.

## Integración continua

GitHub Actions ejecuta `./mvnw verify` en cada push y en cada pull request (`.github/workflows/ci.yml`). Si una ejecución sale en rojo, abre el registro y busca el primer test que falla: el propio test dice qué se esperaba.

## Notas

- La base de datos es H2 en memoria: los datos se pierden al reiniciar. El esquema lo crea Flyway (`src/main/resources/db/migration`).
- Las citas de ejemplo se crean al arrancar. Para desactivarlas, usa `clinica.demo-data=false`.
- No se envía ningún correo de verdad. Los recordatorios se preparan cada hora y se guardan en el buzón de recepción; la confirmación de una reserva se muestra en pantalla.
- El registro de acciones no guarda nombres, correos ni códigos de cita.
- Las citas canceladas no se borran: pasan a estado `CANCELADA` y el hueco vuelve a quedar libre.
- Estados de una cita: `CONFIRMADA`, `CANCELADA`, `ATENDIDA` y `NO_PRESENTADO`. La agenda solo cambia las confirmadas.
