package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Track data transfer object")
public record TrackDTO(
        @Schema(description = "Track identifier", example = "5")
        Long id,

        @NotBlank
        @Size(max = 160)
        @Schema(description = "Track title", example = "Save Your Tears")
        String title,

        @NotNull
        @Min(1)
        @Schema(description = "Track duration in seconds", example = "215")
        Integer durationSec,

        @Schema(description = "Audio file URL", example = "https://example.com/audio/save-your-tears.mp3")
        String audioUrl,

        @NotNull
        @Positive
        @Schema(description = "Album identifier owning the track", example = "1")
        Long albumId
) {
}