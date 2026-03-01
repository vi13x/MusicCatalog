package com.example.musiccatalog.handler;

import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR = "Validation error";
    private static final String FIELD_SEPARATOR = ": ";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + FIELD_SEPARATOR + fe.getDefaultMessage())
                .orElse(VALIDATION_ERROR);
        return build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleAny(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
        String safeMessage = Objects.requireNonNullElse(message, "");
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                safeMessage,
                path
        );
        return ResponseEntity.status(status).body(body);
    }
}