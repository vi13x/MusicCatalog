package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;

public final class ArtistMapper {

    private ArtistMapper() {
    }

    public static ArtistDTO toDto(Artist a) {
        return new ArtistDTO(a.getId(), a.getName());
    }
}