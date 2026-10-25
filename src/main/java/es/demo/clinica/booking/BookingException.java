package es.demo.clinica.booking;

/** La reserva no cumple las reglas de la clínica. El mensaje es válido para mostrar al paciente. */
public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }
}
