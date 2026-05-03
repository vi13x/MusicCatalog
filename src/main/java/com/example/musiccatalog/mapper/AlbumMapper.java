package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AlbumMapper {

    private AlbumMapper() {
    }

    public static AlbumDTO toDto(Album a) {
        Set<Long> genreIds = a.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<TrackDTO> tracks = a.getTracks().stream()
                .collect(Collectors.toMap(
                        AlbumMapper::trackKey,
                        track -> track,
                        (left, right) -> left,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .map(TrackMapper::toDto)
                .toList();

        return new AlbumDTO(
                a.getId(),
                a.getTitle(),
                a.getYear(),
                a.getArtist().getId(),
                genreIds,
                tracks
        );
    }

    private static Object trackKey(Track track) {
        return track.getId() != null ? track.getId() : new UnsavedTrackKey(track.getTitle(), track.getDurationSec(), track.getAlbum());
    }

    private record UnsavedTrackKey(String title, Integer durationSec, Album album) {
        private UnsavedTrackKey {
            Objects.requireNonNull(title);
            Objects.requireNonNull(durationSec);
            Objects.requireNonNull(album);
        }
    }
}
