package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.ArtistMapper;
import com.example.musiccatalog.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistDTO> getAll() {
        return artistRepository.findAll().stream().map(ArtistMapper::toDto).toList();
    }

    public ArtistDTO getById(Long id) {
        return ArtistMapper.toDto(getEntity(id));
    }

    public ArtistDTO create(ArtistDTO dto) {
        Artist a = new Artist(dto.name());
        return ArtistMapper.toDto(artistRepository.save(a));
    }

    public ArtistDTO update(Long id, ArtistDTO dto) {
        Artist a = getEntity(id);
        a.setName(dto.name());
        return ArtistMapper.toDto(artistRepository.save(a));
    }

    public void delete(Long id) {
        artistRepository.delete(getEntity(id));
    }

    public Artist getEntity(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));
    }
}