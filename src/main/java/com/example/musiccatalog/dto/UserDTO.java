package com.example.musiccatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        Long id,
        @NotBlank @Size(max = 255) String name
) {
}
