package br.com.octopus.projectA.Util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShiftAlreadyExistsException.class)
    public ResponseEntity<String> handleShiftAlreadyExistsException(ShiftAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // Outros manipuladores de exceção...
}
