# Taller: lista de espera cuando no hay huecos

Funcionalidad pendiente para el taller final. No está implementada.

## Contexto

Cuando un paciente elige especialista, tratamiento y día, y no hay huecos libres, la web solo muestra que no hay disponibilidad. El paciente se queda sin opción y recepción tiene que llamarle a mano si alguien cancela.

Recepción quiere que el paciente pueda apuntarse a una lista de espera para ese día, y que reciba un aviso cuando se libere un hueco.

## Objetivo

1. El paciente deja su nombre y su correo en la lista de espera de un especialista, un tratamiento y un día.
2. Cuando una cita confirmada de ese especialista se cancela y su hueco queda libre, se avisa a los pacientes apuntados a ese día.

## Criterios de aceptación

1. Si el día no tiene huecos, el paciente puede apuntarse y queda guardada una entrada con nombre, correo, especialista, tratamiento y día.
2. No se puede apuntar a un día pasado, ni a un sábado o domingo, ni a una hora que no sea laborable. Se aplican las mismas reglas que en `AppointmentService`.
3. Un mismo correo no puede apuntarse dos veces al mismo día, especialista y tratamiento.
4. Cuando una cita confirmada se cancela, por el paciente o por recepción, se crea una notificación en el buzón para cada persona apuntada a ese día, con el tipo `HUECO_LIBRE`.
5. Cada entrada recibe el aviso una sola vez, aunque se cancelen otras citas el mismo día.
6. Los textos nuevos existen en español e inglés, y `MessagesBundleTest` sigue en verde.
7. Ningún test que ya pasa antes de empezar deja de pasar.

## Límites

- No se cambian las reglas de horario ni de días de `AppointmentService`.
- No se cambia la firma de métodos públicos de otras clases sin confirmarlo antes.
- No se añaden dependencias nuevas en `pom.xml`.
- Comentarios y textos nuevos en castellano, y en inglés en los mensajes.

## Pistas

- Hace falta una entidad nueva para la lista de espera y una migración Flyway `V8__lista_espera.sql`. El proyecto usa `ddl-auto=validate`, así que el esquema tiene que coincidir exactamente con la entidad.
- Para saber si un día tiene huecos, reutiliza `AppointmentService.availableSlots`.
- Para el aviso, reutiliza `Notification` y `NotificationRepository`, igual que `ReminderService`.
- Las cancelaciones pasan por `AppointmentService.cancel` (recepción) y `AppointmentService.cancelByCode` (paciente).
- Si quieres registrar el aviso en auditoría, `AuditService` ya existe. Mira si `AuditAction` necesita un valor nuevo. La columna `event_type` tiene 30 caracteres.

## Cómo saber que está terminado

- `./mvnw -B verify` pasa, salvo los tests que ya fallaban antes de empezar.
- Hay al menos un test nuevo por cada criterio de aceptación.
- El cambio se puede contar en pocas frases en el mensaje de commit, en formato Conventional Commits.
