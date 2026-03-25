package com.example.musiccatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User data transfer object")
public record UserDTO(
        @Schema(description = "User identifier", example = "7")
        Long id,
        @NotBlank
        @Size(max = 255)
        @Schema(description = "User name", example = "Alice")
        String name
) {
}
