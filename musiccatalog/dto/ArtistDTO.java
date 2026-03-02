package com.example.musiccatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistDTO(
        Long id,
        @NotBlank @Size(max = 120) String name
) {
}