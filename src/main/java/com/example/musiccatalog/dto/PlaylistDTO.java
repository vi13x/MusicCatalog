package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Playlist data transfer object")
public record PlaylistDTO(
        @Schema(description = "Playlist identifier", example = "10")
        Long id,
        @NotBlank
        @Size(max = 120)
        @Schema(description = "Playlist name", example = "Road Trip")
        String name,
        @Schema(description = "Track identifiers included in the playlist")
        Set<@Positive Long> trackIds
) {
}
