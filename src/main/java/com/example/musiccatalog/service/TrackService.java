package com.example.musiccatalog.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.entity.constant.EntityType;
import com.example.musiccatalog.exception.EntityNotFoundException;
import com.example.musiccatalog.mapper.TrackMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.GenreRepository;
import com.example.musiccatalog.repository.TrackRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final TrackMapper trackMapper;

    public TrackDTO create(TrackDTO dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new IllegalArgumentException("Track title is blank");
        }
        if (dto.album() == null || dto.album().id() == null) {
            throw new IllegalArgumentException("Album id is required");
        }
        if (dto.durationSeconds() == null || dto.durationSeconds() <= 0) {
            throw new IllegalArgumentException("Track duration must be positive");
        }

        Album album = albumRepository.findById(dto.album().id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ALBUM, "id", dto.album().id()));

        Track track = trackMapper.toEntity(dto);
        track.setAlbum(album);

        if (dto.genres() != null && !dto.genres().isEmpty()) {
            Set<Genre> genres = new HashSet<>(genreRepository.findByNameIn(dto.genres()));
            track.setGenres(genres);
        }

        track = trackRepository.save(track);
        return trackMapper.toDTO(track);
    }

    public TrackDTO findById(long id) {
        Track entity = trackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.TRACK, "id", id));
        return trackMapper.toDTO(entity);
    }

    public List<TrackDTO> findAll() {
        return trackRepository.findAll().stream()
                .map(trackMapper::toDTO)
                .toList();
    }

    public TrackDTO update(TrackDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Track id is required for update");
        }
        if (dto.title() == null || dto.title().isBlank()) {
            throw new IllegalArgumentException("Track title is blank");
        }

        Track track = trackRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.TRACK, "id", dto.id()));

        track.setTitle(dto.title().trim());
        track.setDurationSeconds(dto.durationSeconds());

        if (dto.album() != null && dto.album().id() != null) {
            Album album = albumRepository.findById(dto.album().id())
                    .orElseThrow(() -> new EntityNotFoundException(EntityType.ALBUM, "id", dto.album().id()));
            track.setAlbum(album);
        }

        if (dto.genres() != null) {
            Set<Genre> genres = dto.genres().isEmpty()
                    ? new HashSet<>()
                    : new HashSet<>(genreRepository.findByNameIn(dto.genres()));
            track.setGenres(genres);
        }

        track = trackRepository.save(track);
        return trackMapper.toDTO(track);
    }

    public void removeById(long id) {
        trackRepository.deleteById(id);
    }
}
