package com.example.musiccatalog.dto;

import com.example.musiccatalog.service.AsyncTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Accepted async task metadata")
public record AsyncTaskAcceptedDTO(
        @Schema(description = "Async task identifier", example = "1")
        Long taskId,
        @Schema(description = "Current task status", example = "QUEUED")
        AsyncTaskStatus status,
        @Schema(description = "Task creation timestamp", example = "2026-04-12T10:15:30Z")
        Instant createdAt
) {
}
