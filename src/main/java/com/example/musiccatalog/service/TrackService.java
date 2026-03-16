package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.TrackMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.TrackRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final AlbumSearchIndex albumSearchIndex;

    public TrackService(TrackRepository trackRepository,
                        AlbumRepository albumRepository,
                        AlbumSearchIndex albumSearchIndex) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.albumSearchIndex = albumSearchIndex;
    }

    @Transactional(readOnly = true)
    public List<TrackDTO> getAll() {
        return trackRepository.findAll().stream().map(TrackMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TrackDTO getById(Long id) {
        return TrackMapper.toDto(getEntity(id));
    }

    @Transactional
    public TrackDTO create(TrackDTO dto) {
        Album album = albumRepository.findById(dto.albumId())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + dto.albumId()));

        Track track = new Track(dto.title(), dto.durationSec());
        track.setAlbum(album);
        TrackDTO saved = TrackMapper.toDto(trackRepository.save(track));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public TrackDTO update(Long id, TrackDTO dto) {
        Track track = getEntity(id);
        track.setTitle(dto.title());
        track.setDurationSec(dto.durationSec());
        TrackDTO saved = TrackMapper.toDto(trackRepository.save(track));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        trackRepository.delete(getEntity(id));
        albumSearchIndex.clear();
    }

    private Track getEntity(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.TRACK_NOT_FOUND + id));
    }
}