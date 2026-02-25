package com.example.musiccatalog.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.musiccatalog.dto.AlbumRefDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;

@Component
public class TrackMapper {

    public Track toEntity(TrackDTO dto) {
        Track track = new Track();
        track.setTitle(dto.title());
        track.setDurationSec(dto.durationSec());
        return track;
    }

    public TrackDTO toDTO(Track entity) {
        Album album = entity.getAlbum();
        AlbumRefDTO albumRef = album != null
                ? new AlbumRefDTO(album.getId(), album.getTitle())
                : null;

        Set<String> genreNames = entity.getGenres() != null
                ? entity.getGenres().stream().map(Genre::getName).collect(Collectors.toSet())
                : Set.of();

        return new TrackDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDurationSec(),
                albumRef,
                genreNames);
    }
}
