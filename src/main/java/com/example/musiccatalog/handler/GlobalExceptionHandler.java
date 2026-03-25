package com.example.musiccatalog.handler;

import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String VALIDATION_ERROR = "Request validation failed";
    private static final String INTERNAL_ERROR = "Unexpected internal server error";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        log.warn("Bad request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ValidationErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toValidationError)
                .toList();
        log.warn("Body validation failed for {} {}: {}", req.getMethod(), req.getRequestURI(), details);
        return build(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, VALIDATION_ERROR, req, details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidation(HandlerMethodValidationException ex,
                                                                  HttpServletRequest req) {
        List<ValidationErrorDetail> details = ex.getAllValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new ValidationErrorDetail(
                                Objects.requireNonNullElse(result.getMethodParameter().getParameterName(), "argument"),
                                Objects.requireNonNullElse(error.getDefaultMessage(), VALIDATION_ERROR),
                                stringify(result.getArgument())
                        )))
                .toList();
        log.warn("Method validation failed for {} {}: {}", req.getMethod(), req.getRequestURI(), details);
        return build(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, VALIDATION_ERROR, req, details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ValidationErrorDetail> details = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        stringify(violation.getInvalidValue())
                ))
                .toList();
        log.warn("Constraint validation failed for {} {}: {}", req.getMethod(), req.getRequestURI(), details);
        return build(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, VALIDATION_ERROR, req, details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex,
                                                           HttpServletRequest req) {
        ValidationErrorDetail detail = new ValidationErrorDetail(
                ex.getParameterName(),
                "Required request parameter is missing",
                null
        );
        log.warn("Missing request parameter on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", ex.getMessage(), req, List.of(detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        ValidationErrorDetail detail = new ValidationErrorDetail(
                Objects.requireNonNullElse(ex.getName(), "argument"),
                "Value has invalid type",
                stringify(ex.getValue())
        );
        log.warn("Type mismatch on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "Request parameter has invalid type", req, List.of(detail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        log.warn("Malformed request body on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Malformed JSON request body", req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        if (msg != null && msg.contains("foreign key")) {
            msg = "Cannot delete or update: the entity is still referenced by others (e.g. artist has albums).";
        }
        msg = Objects.requireNonNullElse(msg, "Data integrity violation");
        log.warn("Data integrity violation on {} {}: {}", req.getMethod(), req.getRequestURI(), msg);
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", msg, req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", INTERNAL_ERROR, req, List.of());
    }

    private ValidationErrorDetail toValidationError(FieldError error) {
        return new ValidationErrorDetail(
                error.getField(),
                Objects.requireNonNullElse(error.getDefaultMessage(), VALIDATION_ERROR),
                stringify(error.getRejectedValue())
        );
    }

    private String stringify(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private ResponseEntity<ApiError> build(HttpStatus status,
                                           String code,
                                           String message,
                                           HttpServletRequest request,
                                           List<ValidationErrorDetail> details) {
        String safeMessage = Objects.requireNonNullElse(message, "");
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                safeMessage,
                request.getRequestURI(),
                request.getMethod(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
