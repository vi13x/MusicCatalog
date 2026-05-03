package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;

public final class GenreMapper {

    private GenreMapper() {
    }

    public static GenreDTO toDto(Genre g) {
        return new GenreDTO(g.getId(), g.getName());
    }
}