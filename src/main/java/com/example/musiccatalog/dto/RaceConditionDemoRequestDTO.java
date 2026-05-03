package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Parameters for race condition demonstration")
public record RaceConditionDemoRequestDTO(
        @NotNull
        @Min(50)
        @Schema(description = "Number of concurrent threads", example = "64", minimum = "50")
        Integer threads,
        @NotNull
        @Min(1)
        @Schema(description = "Increment operations per thread", example = "5000")
        Integer incrementsPerThread
) {
}
