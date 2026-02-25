package com.example.musiccatalog.dto;

import java.util.Set;

public record PlaylistDTO(Long id, String name, Set<TrackDTO> tracks) {
}
