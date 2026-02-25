package com.example.musiccatalog.mapper;

import org.springframework.stereotype.Component;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;

@Component
public class ArtistMapper {

    public Artist toEntity(ArtistDTO dto) {
        Artist artist = new Artist();
        if (dto.name() != null) {
            artist.setName(dto.name());
        }
        return artist;
    }

    public ArtistDTO toDTO(Artist entity) {
        return new ArtistDTO(entity.getId(), entity.getName());
    }
}
