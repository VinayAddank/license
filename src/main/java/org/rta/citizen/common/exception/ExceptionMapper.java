package org.rta.citizen.common.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionMapper {

    @ExceptionHandler
    public ResponseEntity<ValidationError> handleException(MethodArgumentNotValidException exception) {
        return createValidationError(exception);
    }

    private ResponseEntity<ValidationError> createValidationError(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(e.getBindingResult()));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
