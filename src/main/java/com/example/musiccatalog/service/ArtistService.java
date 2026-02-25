package com.example.musiccatalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.constant.EntityType;
import com.example.musiccatalog.exception.EntityNotFoundException;
import com.example.musiccatalog.mapper.ArtistMapper;
import com.example.musiccatalog.repository.ArtistRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistDTO create(ArtistDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Artist name is blank");
        }
        String trimmed = dto.name().trim();
        if (artistRepository.existsByName(trimmed)) {
            throw new IllegalArgumentException("Artist already exists: " + trimmed);
        }
        Artist entity = artistMapper.toEntity(dto);
        entity.setName(trimmed);
        entity = artistRepository.save(entity);
        return artistMapper.toDTO(entity);
    }

    public ArtistDTO findById(long id) {
        Artist entity = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ARTIST, "id", id));
        return artistMapper.toDTO(entity);
    }

    public ArtistDTO findByName(String name) {
        Artist entity = artistRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ARTIST, "name", name));
        return artistMapper.toDTO(entity);
    }

    public List<ArtistDTO> findAll() {
        return artistRepository.findAll().stream()
                .map(artistMapper::toDTO)
                .toList();
    }

    public ArtistDTO update(ArtistDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Artist id is required for update");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Artist name is blank");
        }
        Artist entity = artistRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ARTIST, "id", dto.id()));
        entity.setName(dto.name().trim());
        entity = artistRepository.save(entity);
        return artistMapper.toDTO(entity);
    }

    public void removeById(long id) {
        artistRepository.deleteById(id);
    }
}
