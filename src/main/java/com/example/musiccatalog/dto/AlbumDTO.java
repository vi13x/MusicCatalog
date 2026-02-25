package com.example.musiccatalog.dto;

import java.util.List;

public record AlbumDTO(
        Long id,
        String title,
        Integer releaseYear,
        ArtistDTO artist,
        List<TrackDTO> tracks) {
}
