package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Artist data transfer object")
public record ArtistDTO(
        @Schema(description = "Artist identifier", example = "1")
        Long id,
        @NotBlank
        @Size(max = 120)
        @Schema(description = "Artist name", example = "Metallica")
        String name
) {
}
