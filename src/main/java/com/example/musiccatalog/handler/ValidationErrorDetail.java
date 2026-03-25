package com.example.musiccatalog.handler;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error detail")
public record ValidationErrorDetail(
        @Schema(description = "Field or parameter name", example = "title")
        String field,
        @Schema(description = "Validation message", example = "must not be blank")
        String message,
        @Schema(description = "Rejected value as string", example = "")
        String rejectedValue
) {
}
