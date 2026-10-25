package es.demo.clinica.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convierte las reglas incumplidas en un error 422 con formato Problem Details (RFC 9457). */
@RestControllerAdvice(assignableTypes = AppointmentApiController.class)
class BookingExceptionHandler {

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ProblemDetail handle(BookingException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problem.setTitle("La reserva no cumple las reglas de la clínica");
        return problem;
    }
}
