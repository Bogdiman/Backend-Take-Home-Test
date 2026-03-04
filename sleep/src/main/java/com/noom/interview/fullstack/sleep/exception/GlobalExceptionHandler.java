package com.noom.interview.fullstack.sleep.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("messages", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        log.warn("Missing required header: {}", ex.getHeaderName());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Missing Required Header");
        response.put("message", String.format("Required header '%s' is missing", ex.getHeaderName()));

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body";
        
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().isEmpty() ? "field" : 
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                Object invalidValue = ife.getValue();
                
                if (ife.getTargetType().equals(MorningFeeling.class)) {
                    String validValues = Arrays.stream(MorningFeeling.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));
                    message = String.format("Invalid value '%s' for %s. Must be one of: %s", 
                            invalidValue, fieldName, validValues);
                } else {
                    Object[] enumConstants = ife.getTargetType().getEnumConstants();
                    String validValues = Arrays.stream(enumConstants)
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                    message = String.format("Invalid value '%s' for %s. Must be one of: %s", 
                            invalidValue, fieldName, validValues);
                }
            }
        }

        log.warn("HTTP message not readable: {}", message);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Request");
        response.put("message", message);

        return ResponseEntity.badRequest().body(response);
    }
}
