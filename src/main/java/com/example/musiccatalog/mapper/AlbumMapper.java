package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.AlbumDto;
import com.example.musiccatalog.entity.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {
    public AlbumDto toDto(Album album) {
        if (album == null) return null;
        return new AlbumDto(album.getId(), album.getTitle(), album.getArtist(), album.getYear());
    }
}
