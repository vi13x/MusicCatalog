package com.example.musiccatalog.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.entity.Playlist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.entity.constant.EntityType;
import com.example.musiccatalog.exception.EntityNotFoundException;
import com.example.musiccatalog.mapper.PlaylistMapper;
import com.example.musiccatalog.repository.PlaylistRepository;
import com.example.musiccatalog.repository.TrackRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistMapper playlistMapper;

    public PlaylistDTO create(PlaylistDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Playlist name is blank");
        }

        Playlist playlist = playlistMapper.toEntity(dto);
        playlist.setName(dto.name().trim());

        if (dto.tracks() != null && !dto.tracks().isEmpty()) {
            Set<Long> trackIds = dto.tracks().stream()
                    .map(t -> t.id())
                    .filter(Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());
            if (!trackIds.isEmpty()) {
                Set<Track> tracks = new HashSet<>(trackRepository.findAllById(trackIds));
                playlist.setTracks(tracks);
            }
        }

        playlist = playlistRepository.save(playlist);
        return playlistMapper.toDTO(playlist);
    }

    public PlaylistDTO findById(long id) {
        Playlist entity = playlistRepository.findWithTracksById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.PLAYLIST, "id", id));
        return playlistMapper.toDTO(entity);
    }

    public List<PlaylistDTO> findAll() {
        return playlistRepository.findAll().stream()
                .map(playlistMapper::toDTO)
                .toList();
    }

    public PlaylistDTO update(PlaylistDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Playlist id is required for update");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Playlist name is blank");
        }

        Playlist playlist = playlistRepository.findWithTracksById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.PLAYLIST, "id", dto.id()));

        playlist.setName(dto.name().trim());

        if (dto.tracks() != null) {
            Set<Long> trackIds = dto.tracks().stream()
                    .map(t -> t.id())
                    .filter(Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());
            Set<Track> tracks = trackIds.isEmpty()
                    ? new HashSet<>()
                    : new HashSet<>(trackRepository.findAllById(trackIds));
            playlist.setTracks(tracks);
        }

        playlist = playlistRepository.save(playlist);
        return playlistMapper.toDTO(playlist);
    }

    public void removeById(long id) {
        playlistRepository.deleteById(id);
    }
}