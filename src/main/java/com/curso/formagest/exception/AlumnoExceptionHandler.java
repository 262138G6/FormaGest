
package com.curso.formagest.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles validation and database constraint errors
 * for the FormaGest student REST controller.
 *
 * Returns clear JSON responses instead of technical
 * exception details.
 */
@RestControllerAdvice(
        basePackages = "com.curso.formagest.controller",
        assignableTypes = {
                com.curso.formagest.controller.AlumnoController.class
        }
)
public class AlumnoExceptionHandler {

    /**
     * Handles invalid student data received in POST or PUT requests.
     *
     * @param exception the validation exception.
     * @return HTTP 400 with validation errors grouped by field.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        // Stores validation messages associated with each field.
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        // Builds a readable JSON error response.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Error de validación");
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handles database constraint violations.
     *
     * This includes duplicate student email addresses.
     *
     * @param exception the database constraint exception.
     * @return HTTP 409 with a readable conflict message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConflict(
            DataIntegrityViolationException exception) {

        // Builds a generic message without exposing database details.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put(
                "message",
                "No se puede guardar el alumno por un conflicto de datos"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}
