package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.GenreMapper;
import com.example.musiccatalog.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDTO> getAll() {
        return genreRepository.findAll().stream().map(GenreMapper::toDto).toList();
    }

    public GenreDTO getById(Long id) {
        return GenreMapper.toDto(getEntity(id));
    }

    public GenreDTO create(GenreDTO dto) {
        Genre g = new Genre(dto.name());
        return GenreMapper.toDto(genreRepository.save(g));
    }

    public GenreDTO update(Long id, GenreDTO dto) {
        Genre g = getEntity(id);
        g.setName(dto.name());
        return GenreMapper.toDto(genreRepository.save(g));
    }

    public void delete(Long id) {
        genreRepository.delete(getEntity(id));
    }

    public Genre getEntity(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.GENRE_NOT_FOUND + id));
    }
}