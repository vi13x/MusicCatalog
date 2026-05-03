package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Schema(description = "Album data transfer object")
public record AlbumDTO(
        @Schema(description = "Album identifier", example = "1")
        Long id,
        @NotBlank
        @Size(max = 160)
        @Schema(description = "Album title", example = "Master of Puppets")
        String title,
        @NotNull
        @Min(1900)
        @Max(2100)
        @Schema(description = "Album release year", example = "1986")
        Integer year,
        @NotNull
        @Positive
        @Schema(description = "Artist identifier", example = "2")
        Long artistId,
        @Schema(description = "Genre identifiers assigned to the album")
        Set<@Positive Long> genreIds,
        @Schema(description = "Tracks to create or return with the album")
        List<TrackDTO> tracks
) {
}
