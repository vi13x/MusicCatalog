package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Track payload used for bulk creation inside a single album")
public record TrackBulkCreateItemDTO(
        @NotBlank
        @Size(max = 160)
        @Schema(description = "Track title", example = "Battery")
        String title,
        @NotNull
        @Min(1)
        @Schema(description = "Track duration in seconds", example = "312")
        Integer durationSec
) {
}
