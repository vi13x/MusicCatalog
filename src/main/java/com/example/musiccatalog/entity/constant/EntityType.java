package com.example.musiccatalog.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
    ARTIST("Artist"),
    ALBUM("Album"),
    TRACK("Track"),
    GENRE("Genre"),
    PLAYLIST("Playlist");

    private final String name;
}
