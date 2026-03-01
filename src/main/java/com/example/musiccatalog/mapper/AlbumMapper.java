package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Genre;

import java.util.Set;
import java.util.stream.Collectors;

public final class AlbumMapper {

    private AlbumMapper() {
    }

    public static AlbumDTO toDto(Album a) {
        Set<Long> genreIds = a.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        return new AlbumDTO(
                a.getId(),
                a.getTitle(),
                a.getYear(),
                a.getArtist().getId(),
                genreIds,
                a.getTracks().stream().map(TrackMapper::toDto).toList()
        );
    }
}