package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.ArtistMapper;
import com.example.musiccatalog.repository.ArtistRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumSearchIndex albumSearchIndex;

    public ArtistService(ArtistRepository artistRepository, AlbumSearchIndex albumSearchIndex) {
        this.artistRepository = artistRepository;
        this.albumSearchIndex = albumSearchIndex;
    }

    @Transactional(readOnly = true)
    public List<ArtistDTO> getAll() {
        return artistRepository.findAll().stream().map(ArtistMapper::toDto).toList();
    }

    public ArtistDTO getById(Long id) {
        return ArtistMapper.toDto(getEntity(id));
    }

    @Transactional
    public ArtistDTO create(ArtistDTO dto) {
        Artist artist = new Artist(dto.name());
        ArtistDTO saved = ArtistMapper.toDto(artistRepository.save(artist));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public ArtistDTO update(Long id, ArtistDTO dto) {
        Artist artist = getEntity(id);
        artist.setName(dto.name());
        ArtistDTO saved = ArtistMapper.toDto(artistRepository.save(artist));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Artist artist = artistRepository.findByIdWithAlbums(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ARTIST_NOT_FOUND + id));
        artistRepository.delete(artist);
        albumSearchIndex.clear();
    }

    public Artist getEntity(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ARTIST_NOT_FOUND + id));
    }
}