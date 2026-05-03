package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Genre data transfer object")
public record GenreDTO(
        @Schema(description = "Genre identifier", example = "3")
        Long id,
        @NotBlank
        @Size(max = 80)
        @Schema(description = "Genre name", example = "Heavy Metal")
        String name
) {
}
