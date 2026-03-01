package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.entity.Playlist;

import java.util.Set;
import java.util.stream.Collectors;

public final class PlaylistMapper {

    private PlaylistMapper() {
    }

    public static PlaylistDTO toDto(Playlist p) {
        Set<Long> trackIds = p.getTracks().stream()
                .map(t -> t.getId())
                .collect(Collectors.toSet());

        return new PlaylistDTO(p.getId(), p.getName(), trackIds);
    }
}