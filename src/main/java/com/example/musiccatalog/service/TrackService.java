package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.TrackMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;

    public TrackService(TrackRepository trackRepository, AlbumRepository albumRepository) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
    }

    public List<TrackDTO> getAll() {
        return trackRepository.findAll().stream().map(TrackMapper::toDto).toList();
    }

    public TrackDTO getById(Long id) {
        return TrackMapper.toDto(getEntity(id));
    }

    public TrackDTO create(TrackDTO dto) {
        Album album = albumRepository.findById(dto.albumId())
                .orElseThrow(() -> new NotFoundException("Album not found: " + dto.albumId()));

        Track t = new Track(dto.title(), dto.durationSec());
        t.setAlbum(album);
        return TrackMapper.toDto(trackRepository.save(t));
    }

    public TrackDTO update(Long id, TrackDTO dto) {
        Track t = getEntity(id);
        t.setTitle(dto.title());
        t.setDurationSec(dto.durationSec());
        return TrackMapper.toDto(trackRepository.save(t));
    }

    public void delete(Long id) {
        trackRepository.delete(getEntity(id));
    }

    private Track getEntity(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Track not found: " + id));
    }
}