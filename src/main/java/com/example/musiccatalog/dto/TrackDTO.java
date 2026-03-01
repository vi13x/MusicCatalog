package com.example.musiccatalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrackDTO(
        Long id,
        @NotBlank @Size(max = 160) String title,
        @NotNull @Min(1) Integer durationSec,
        @NotNull Long albumId
) {
}