package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Track;

public final class TrackMapper {

    private TrackMapper() {
    }

    public static TrackDTO toDto(Track t) {
        return new TrackDTO(
                t.getId(),
                t.getTitle(),
                t.getDurationSec(),
                t.getAlbum().getId()
        );
    }
}