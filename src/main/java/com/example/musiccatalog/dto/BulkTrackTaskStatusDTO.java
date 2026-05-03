package com.example.musiccatalog.dto;

import com.example.musiccatalog.service.AsyncTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Status of asynchronous bulk track creation in an album")
public record BulkTrackTaskStatusDTO(
        @Schema(description = "Async task identifier", example = "1")
        Long taskId,
        @Schema(description = "Album identifier", example = "23")
        Long albumId,
        @Schema(description = "Current task status", example = "COMPLETED")
        AsyncTaskStatus status,
        @Schema(description = "Task creation timestamp", example = "2026-04-12T10:15:30Z")
        Instant createdAt,
        @Schema(description = "Task start timestamp", example = "2026-04-12T10:15:31Z")
        Instant startedAt,
        @Schema(description = "Task completion timestamp", example = "2026-04-12T10:15:32Z")
        Instant finishedAt,
        @Schema(description = "Created tracks when task is completed")
        List<TrackDTO> result,
        @Schema(description = "Error message when task is failed", example = "Track title already exists in album: albumId=23, title=Battery")
        String errorMessage
) {
}
