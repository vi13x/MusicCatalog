package com.example.musiccatalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record AlbumDTO(
        Long id,
        @NotBlank @Size(max = 160) String title,
        @NotNull @Min(1900) @Max(2100) Integer year,
        @NotNull Long artistId,
        Set<Long> genreIds,
        List<TrackDTO> tracks
) {
}