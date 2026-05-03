package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a single counter run")
public record CounterRunResultDTO(
        @Schema(description = "Counter implementation type", example = "safe")
        String counterType,
        @Schema(description = "Expected final value", example = "320000")
        long expectedValue,
        @Schema(description = "Actual final value", example = "320000")
        long actualValue,
        @Schema(description = "Number of lost updates", example = "0")
        long lostUpdates,
        @Schema(description = "Whether the implementation is thread-safe", example = "true")
        boolean threadSafe,
        @Schema(description = "Elapsed time in milliseconds", example = "41")
        long durationMs
) {
}
