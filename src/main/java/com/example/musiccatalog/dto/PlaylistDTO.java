package com.example.musiccatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PlaylistDTO(
        Long id,
        @NotBlank @Size(max = 120) String name,
        Set<Long> trackIds
) {
}