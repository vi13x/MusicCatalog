package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AlbumDto;
import com.example.musiccatalog.mapper.AlbumMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository repository;
    private final AlbumMapper mapper;

    public AlbumService(AlbumRepository repository, AlbumMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AlbumDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public Optional<AlbumDto> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Optional<AlbumDto> findByTitle(String title) {
        return repository.findByTitle(title)
                .map(mapper::toDto);
    }
}