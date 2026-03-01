package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.entity.Playlist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.PlaylistMapper;
import com.example.musiccatalog.repository.PlaylistRepository;
import com.example.musiccatalog.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    public PlaylistService(PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }

    public List<PlaylistDTO> getAll() {
        return playlistRepository.findAll().stream().map(PlaylistMapper::toDto).toList();
    }

    public PlaylistDTO getById(Long id) {
        return PlaylistMapper.toDto(getEntity(id));
    }

    public PlaylistDTO create(PlaylistDTO dto) {
        Playlist p = new Playlist(dto.name());
        applyTrackIds(p, dto.trackIds());
        return PlaylistMapper.toDto(playlistRepository.save(p));
    }

    public PlaylistDTO update(Long id, PlaylistDTO dto) {
        Playlist p = getEntity(id);
        p.setName(dto.name());
        applyTrackIds(p, dto.trackIds());
        return PlaylistMapper.toDto(playlistRepository.save(p));
    }

    public void delete(Long id) {
        playlistRepository.delete(getEntity(id));
    }

    private void applyTrackIds(Playlist playlist, Set<Long> trackIds) {
        Set<Long> ids = trackIds == null ? new HashSet<>() : new HashSet<>(trackIds);
        Set<Track> tracks = new HashSet<>();
        for (Long tid : ids) {
            Track t = trackRepository.findById(tid)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.TRACK_NOT_FOUND + tid));
            tracks.add(t);
        }
        playlist.setTracks(tracks);
    }

    private Playlist getEntity(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id));
    }
}