package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AlbumDto;
import com.example.musiccatalog.mapper.AlbumMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public List<AlbumDto> findAll() {
        return albumRepository.findAll().stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<AlbumDto> findById(Long id) {
        return albumRepository.findById(id).map(albumMapper::toDto);
    }

    public Optional<AlbumDto> findByTitle(String title) {
        return albumRepository.findByTitle(title).map(albumMapper::toDto);
    }
}
