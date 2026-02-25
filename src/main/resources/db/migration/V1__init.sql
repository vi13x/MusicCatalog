-- V1__init.sql
-- PostgreSQL / schema: public

CREATE SCHEMA IF NOT EXISTS public;

-- =========================
-- ARTISTS
-- =========================
CREATE TABLE IF NOT EXISTS artists (
                                       id           BIGSERIAL PRIMARY KEY,
                                       name         VARCHAR(255) NOT NULL UNIQUE,
    country      VARCHAR(128),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- =========================
-- ALBUMS (важно: именно "albums")
-- =========================
CREATE TABLE IF NOT EXISTS albums (
                                      id            BIGSERIAL PRIMARY KEY,
                                      title         VARCHAR(255) NOT NULL,
    release_year  INT,
    artist_id     BIGINT NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_albums_artist
    FOREIGN KEY (artist_id)
    REFERENCES artists(id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_albums_artist_id ON albums(artist_id);

-- =========================
-- TRACKS
-- =========================
CREATE TABLE IF NOT EXISTS tracks (
                                      id           BIGSERIAL PRIMARY KEY,
                                      title        VARCHAR(255) NOT NULL,
    duration_sec INT NOT NULL CHECK (duration_sec > 0),
    track_no     INT,
    album_id     BIGINT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_tracks_album
    FOREIGN KEY (album_id)
    REFERENCES albums(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_tracks_album_id ON tracks(album_id);

-- =========================
-- GENRES
-- =========================
CREATE TABLE IF NOT EXISTS genres (
                                      id         BIGSERIAL PRIMARY KEY,
                                      name       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- =========================
-- TRACK <-> GENRE (ManyToMany)
-- =========================
CREATE TABLE IF NOT EXISTS track_genres (
                                            track_id BIGINT NOT NULL,
                                            genre_id BIGINT NOT NULL,
                                            PRIMARY KEY (track_id, genre_id),
    CONSTRAINT fk_track_genres_track
    FOREIGN KEY (track_id)
    REFERENCES tracks(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_track_genres_genre
    FOREIGN KEY (genre_id)
    REFERENCES genres(id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_track_genres_genre_id ON track_genres(genre_id);

-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
                                     id         BIGSERIAL PRIMARY KEY,
                                     username   VARCHAR(64) NOT NULL UNIQUE,
    email      VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- =========================
-- PLAYLISTS
-- =========================
CREATE TABLE IF NOT EXISTS playlists (
                                         id          BIGSERIAL PRIMARY KEY,
                                         name        VARCHAR(255) NOT NULL,
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_playlists_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_playlists_user_id ON playlists(user_id);

-- =========================
-- PLAYLIST <-> TRACK (ManyToMany)
-- =========================
CREATE TABLE IF NOT EXISTS playlist_tracks (
                                               playlist_id BIGINT NOT NULL,
                                               track_id    BIGINT NOT NULL,
                                               added_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
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