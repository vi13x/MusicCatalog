-- V1__init.sql
-- Initial schema for music_catalog (PostgreSQL)

-- =========================
-- USERS (for playlists optional association)
-- =========================
CREATE TABLE IF NOT EXISTS users (
                                     id   BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL UNIQUE
    );

-- =========================
-- ARTISTS
-- =========================
CREATE TABLE IF NOT EXISTS artists (
                                       id   BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL UNIQUE
    );

-- =========================
-- GENRES
-- =========================
CREATE TABLE IF NOT EXISTS genres (
                                      id   BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL UNIQUE
    );

-- =========================
-- ALBUMS
-- =========================
CREATE TABLE IF NOT EXISTS albums (
                                      id        BIGSERIAL PRIMARY KEY,
                                      title     VARCHAR(255) NOT NULL,
    year       INTEGER,
    artist_id  BIGINT NOT NULL,
    CONSTRAINT fk_albums_artist
    FOREIGN KEY (artist_id)
    REFERENCES artists(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_albums_artist_id ON albums(artist_id);

-- =========================
-- TRACKS
-- =========================
CREATE TABLE IF NOT EXISTS tracks (
                                      id           BIGSERIAL PRIMARY KEY,
                                      title        VARCHAR(255) NOT NULL,
    duration_sec INTEGER,
    album_id     BIGINT NOT NULL,
    CONSTRAINT fk_tracks_album
    FOREIGN KEY (album_id)
    REFERENCES albums(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_tracks_album_id ON tracks(album_id);

-- =========================
-- ALBUM <-> GENRE (Many-to-Many)
-- =========================
CREATE TABLE IF NOT EXISTS album_genres (
                                            album_id BIGINT NOT NULL,
                                            genre_id BIGINT NOT NULL,
                                            PRIMARY KEY (album_id, genre_id),
    CONSTRAINT fk_album_genres_album
    FOREIGN KEY (album_id)
    REFERENCES albums(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_album_genres_genre
    FOREIGN KEY (genre_id)
    REFERENCES genres(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_album_genres_genre_id ON album_genres(genre_id);

-- =========================
-- PLAYLISTS + PLAYLIST_TRACKS
-- =========================
CREATE TABLE IF NOT EXISTS playlists (
                                         id      BIGSERIAL PRIMARY KEY,
                                         name    VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_playlists_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_playlists_user_id ON playlists(user_id);

CREATE TABLE IF NOT EXISTS playlist_tracks (
                                               playlist_id BIGINT NOT NULL,
                                               track_id    BIGINT NOT NULL,
                                               PRIMARY KEY (playlist_id, track_id),
    CONSTRAINT fk_playlist_tracks_playlist
    FOREIGN KEY (playlist_id)
    REFERENCES playlists(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_playlist_tracks_track
    FOREIGN KEY (track_id)
    REFERENCES tracks(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_playlist_tracks_track_id ON playlist_tracks(track_id);