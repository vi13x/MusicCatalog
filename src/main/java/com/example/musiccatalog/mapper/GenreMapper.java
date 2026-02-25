package com.example.musiccatalog.mapper;

import org.springframework.stereotype.Component;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;

@Component
public class GenreMapper {

    public Genre toEntity(GenreDTO dto) {
        Genre genre = new Genre();
        genre.setName(dto.name());
        return genre;
    }

    public GenreDTO toDTO(Genre entity) {
        return new GenreDTO(entity.getId(), entity.getName());
    }
}
