package com.example.musiccatalog.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Unified API error payload")
public record ApiError(
        @Schema(description = "Time when the error was generated", example = "2026-03-22T12:00:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP reason phrase", example = "Bad Request")
        String error,
        @Schema(description = "Application-level error code", example = "VALIDATION_ERROR")
        String code,
        @Schema(description = "Human-readable error message", example = "Request validation failed")
        String message,
        @Schema(description = "Request path", example = "/api/artists")
        String path,
        @Schema(description = "HTTP method", example = "POST")
        String method,
        @Schema(description = "Validation details when the error is caused by invalid input")
        List<ValidationErrorDetail> validationErrors
) {

    public ApiError {
        validationErrors = validationErrors == null ? List.of() : List.copyOf(validationErrors);
    }
}
