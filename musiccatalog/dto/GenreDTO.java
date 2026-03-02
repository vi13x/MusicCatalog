package com.example.musiccatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreDTO(
        Long id,
        @NotBlank @Size(max = 80) String name
) {
}