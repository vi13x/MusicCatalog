package com.example.musiccatalog.dto;

import java.util.Set;

public record TrackDTO(
        Long id,
        String title,
        Integer durationSec,
        AlbumRefDTO album,
        Set<String> genres) {
}
