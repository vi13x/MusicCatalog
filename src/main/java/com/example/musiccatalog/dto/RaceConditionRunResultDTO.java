package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a single race condition demonstration run")
public record RaceConditionRunResultDTO(
        @Schema(description = "Run mode", example = "safe")
        String mode,
        @Schema(description = "Number of concurrent threads", example = "100")
        int threads,
        @Schema(description = "Increment operations per thread", example = "10000")
        int incrementsPerThread,
        @Schema(description = "Expected final counter value", example = "1000000")
        long expected,
        @Schema(description = "Actual final counter value", example = "1000000")
        long actual,
        @Schema(description = "Number of lost updates", example = "0")
        long lostUpdates,
        @Schema(description = "Execution time in milliseconds", example = "49")
        long durationMs
) {

    public static RaceConditionRunResultDTO from(String mode,
                                                 int threads,
                                                 int incrementsPerThread,
                                                 CounterRunResultDTO result) {
        return new RaceConditionRunResultDTO(
                mode,
                threads,
                incrementsPerThread,
                result.expectedValue(),
                result.actualValue(),
                result.lostUpdates(),
                result.durationMs()
        );
    }
}
