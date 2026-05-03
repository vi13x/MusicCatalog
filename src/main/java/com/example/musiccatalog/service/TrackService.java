package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.TrackMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.TrackRepository;
import java.util.List;
import java.util.Optional;
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
        String normalizedTitle = normalizeTitle(dto.title());
        validateTrackTitle(album.getId(), normalizedTitle);

        Track track = new Track(normalizedTitle, dto.durationSec());
        track.setAlbum(album);
        TrackDTO saved = TrackMapper.toDto(trackRepository.save(track));
        albumSearchIndex.clear();
        return saved;
    }

    @Transactional
    public List<TrackDTO> createBulk(Long albumId, List<TrackBulkCreateItemDTO> request) {
        try {
            return persistBulk(albumId, request);
        } finally {
            albumSearchIndex.clear();
        }
    }

    public List<TrackDTO> createBulkWithoutTransaction(Long albumId, List<TrackBulkCreateItemDTO> request) {
        try {
            return persistBulk(albumId, request);
        } finally {
            albumSearchIndex.clear();
        }
    }

    @Transactional
    public TrackDTO update(Long id, TrackDTO dto) {
        Track track = getEntity(id);
        String normalizedTitle = normalizeTitle(dto.title());
        validateTrackTitle(track.getAlbum().getId(), normalizedTitle, id);
        track.setTitle(normalizedTitle);
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

    private List<TrackDTO> persistBulk(Long albumId, List<TrackBulkCreateItemDTO> request) {
        Album album = getAlbumEntity(albumId);

        return normalizeBulkRequest(request).stream()
                .map(item -> buildTrack(album, item))
                .map(trackRepository::saveAndFlush)
                .map(TrackMapper::toDto)
                .toList();
    }

    private List<TrackBulkCreateItemDTO> normalizeBulkRequest(List<TrackBulkCreateItemDTO> request) {
        return Optional.ofNullable(request)
                .filter(items -> !items.isEmpty())
                .orElseThrow(() -> new BadRequestException(ErrorMessages.BULK_TRACK_REQUEST_EMPTY));
    }

    private Track buildTrack(Album album, TrackBulkCreateItemDTO item) {
        String normalizedTitle = normalizeTitle(item.title());
        validateTrackTitle(album.getId(), normalizedTitle);

        Track track = new Track(normalizedTitle, item.durationSec());
        track.setAlbum(album);
        return track;
    }

    private String normalizeTitle(String title) {
        return Optional.ofNullable(title)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new BadRequestException("Track title must not be blank"));
    }

    private void validateTrackTitle(Long albumId, String title) {
        if (trackRepository.existsByAlbum_IdAndTitleIgnoreCase(albumId, title)) {
            throw new BadRequestException(
                    ErrorMessages.TRACK_TITLE_ALREADY_EXISTS_IN_ALBUM + ": albumId=" + albumId + ", title=" + title
            );
        }
    }

    private void validateTrackTitle(Long albumId, String title, Long trackIdToExclude) {
        if (trackRepository.existsByAlbum_IdAndTitleIgnoreCaseAndIdNot(albumId, title, trackIdToExclude)) {
            throw new BadRequestException(
                    ErrorMessages.TRACK_TITLE_ALREADY_EXISTS_IN_ALBUM + ": albumId=" + albumId + ", title=" + title
            );
        }
    }

    private Album getAlbumEntity(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + albumId));
    }

    private Track getEntity(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.TRACK_NOT_FOUND + id));
    }
}
