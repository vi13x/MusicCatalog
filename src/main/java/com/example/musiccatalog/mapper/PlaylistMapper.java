package com.example.musiccatalog.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Playlist;
import com.example.musiccatalog.entity.Track;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PlaylistMapper {

    private final TrackMapper trackMapper;

    public Playlist toEntity(PlaylistDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setName(dto.name());
        return playlist;
    }

    public PlaylistDTO toDTO(Playlist entity) {
        Set<TrackDTO> tracksDto = entity.getTracks() != null
                ? entity.getTracks().stream().map(trackMapper::toDTO).collect(Collectors.toSet())
                : Set.of();

        return new PlaylistDTO(entity.getId(), entity.getName(), tracksDto);
    }
}
