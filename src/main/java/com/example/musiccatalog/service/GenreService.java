package com.example.musiccatalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.constant.EntityType;
import com.example.musiccatalog.exception.EntityNotFoundException;
import com.example.musiccatalog.mapper.GenreMapper;
import com.example.musiccatalog.repository.GenreRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreDTO create(GenreDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Genre name is blank");
        }
        String trimmed = dto.name().trim();
        if (genreRepository.findByName(trimmed).isPresent()) {
            throw new IllegalArgumentException("Genre already exists: " + trimmed);
        }
        Genre entity = genreMapper.toEntity(dto);
        entity.setName(trimmed);
        entity = genreRepository.save(entity);
        return genreMapper.toDTO(entity);
    }

    public GenreDTO findById(long id) {
        Genre entity = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.GENRE, "id", id));
        return genreMapper.toDTO(entity);
    }

    public List<GenreDTO> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDTO)
                .toList();
    }

    public GenreDTO update(GenreDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Genre id is required for update");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Genre name is blank");
        }
        Genre entity = genreRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.GENRE, "id", dto.id()));
        entity.setName(dto.name().trim());
        entity = genreRepository.save(entity);
        return genreMapper.toDTO(entity);
    }

    public void removeById(long id) {
        genreRepository.deleteById(id);
    }
}
