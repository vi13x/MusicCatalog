package com.example.musiccatalog.exception;

public final class ErrorMessages {

    public static final String ALBUM_NOT_FOUND = "Album not found: ";
    public static final String ARTIST_NOT_FOUND = "Artist not found: ";
    public static final String GENRE_NOT_FOUND = "Genre not found: ";
    public static final String TRACK_NOT_FOUND = "Track not found: ";
    public static final String PLAYLIST_NOT_FOUND = "Playlist not found: ";
    public static final String ASYNC_TASK_NOT_FOUND = "Async task not found: ";
    public static final String INVALID_ALBUM_YEAR_RANGE = "Album search yearFrom must be less than or equal to yearTo";
    public static final String BULK_TRACK_REQUEST_EMPTY = "Bulk track request must contain at least one track";
    public static final String TRACK_TITLE_ALREADY_EXISTS_IN_ALBUM = "Track title already exists in album";

    private ErrorMessages() {
    }
}
