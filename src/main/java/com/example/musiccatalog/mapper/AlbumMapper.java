package com.example.musiccatalog.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AlbumMapper {

    private final TrackMapper trackMapper;

    public Album toEntity(AlbumDTO dto) {
        Album album = new Album();
        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        return album;
    }

    public AlbumDTO toDTO(Album entity) {
        Artist artist = entity.getArtist();
        ArtistDTO artistDto = artist != null
                ? new ArtistDTO(artist.getId(), artist.getName())
                : null;

        List<TrackDTO> tracksDto = entity.getTracks() != null
                ? entity.getTracks().stream().map(trackMapper::toDTO).toList()
                : List.of();

        return new AlbumDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getReleaseYear(),
                artistDto,
                tracksDto);
    }
}
