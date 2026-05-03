package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.GenreMapper;
import com.example.musiccatalog.repository.GenreRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final AlbumSearchIndex albumSearchIndex;

    public GenreService(GenreRepository genreRepository, AlbumSearchIndex albumSearchIndex) {
        this.genreRepository = genreRepository;
        this.albumSearchIndex = albumSearchIndex;
    }

    @Transactional(readOnly = true)
    public List<GenreDTO> getAll() {
        return genreRepository.findAll().stream().map(GenreMapper::toDto).toList();
    }

    public GenreDTO getById(Long id) {
        return GenreMapper.toDto(getEntity(id));
    }

    @Transactional
    public GenreDTO create(GenreDTO dto) {
        Genre genre = new Genre(dto.name());
        GenreDTO saved = GenreMapper.toDto(genreRepository.save(genre));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public GenreDTO update(Long id, GenreDTO dto) {
        Genre genre = getEntity(id);
        genre.setName(dto.name());
        GenreDTO saved = GenreMapper.toDto(genreRepository.save(genre));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        genreRepository.delete(getEntity(id));
        albumSearchIndex.clear();
    }

    public Genre getEntity(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.GENRE_NOT_FOUND + id));
    }
}