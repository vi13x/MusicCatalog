package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comparison of unsafe and thread-safe counter behavior under concurrent load")
public record RaceConditionDemoResultDTO(
        @Schema(description = "Number of concurrent threads", example = "64")
        int threadCount,
        @Schema(description = "Increment operations per thread", example = "5000")
        int incrementsPerThread,
        @Schema(description = "Result of unsafe counter run")
        CounterRunResultDTO unsafeCounter,
        @Schema(description = "Result of safe counter run")
        CounterRunResultDTO safeCounter
) {
}
