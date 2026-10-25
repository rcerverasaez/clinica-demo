package es.demo.clinica.booking;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convierte las reglas incumplidas en un error 422 con formato Problem Details (RFC 9457), traducido. */
@RestControllerAdvice(assignableTypes = AppointmentApiController.class)
class BookingExceptionHandler {

    private final MessageSource messages;

    BookingExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ProblemDetail handle(BookingException e) {
        Locale locale = LocaleContextHolder.getLocale();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY,
                messages.getMessage(e.getMessage(), null, locale));
        problem.setTitle(messages.getMessage("booking.problem.title", null, locale));
        return problem;
    }
}
