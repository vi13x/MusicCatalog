-- V3__seed_data.sql
-- Тестовые данные для music_catalog

-- =========================
-- USERS
-- =========================
INSERT INTO users (name) VALUES
    ('demo-user')
ON CONFLICT (name) DO NOTHING;

-- =========================
-- ARTISTS
-- =========================
INSERT INTO artists (name) VALUES
                               ('The Beatles'),
                               ('Pink Floyd'),
                               ('Led Zeppelin'),
                               ('Queen')
ON CONFLICT (name) DO NOTHING;

-- =========================
-- GENRES
-- =========================
INSERT INTO genres (name) VALUES
                              ('Rock'),
                              ('Progressive Rock'),
                              ('Psychedelic Rock'),
                              ('Hard Rock'),
                              ('Pop Rock')
ON CONFLICT (name) DO NOTHING;

-- =========================
-- ALBUMS
-- =========================
INSERT INTO albums (title, release_year, artist_id)
SELECT 'Abbey Road', 1969, id FROM artists WHERE name = 'The Beatles' LIMIT 1;

INSERT INTO albums (title, release_year, artist_id)
SELECT 'The Dark Side of the Moon', 1973, id FROM artists WHERE name = 'Pink Floyd' LIMIT 1;

INSERT INTO albums (title, release_year, artist_id)
SELECT 'Led Zeppelin IV', 1971, id FROM artists WHERE name = 'Led Zeppelin' LIMIT 1;

INSERT INTO albums (title, release_year, artist_id)
SELECT 'A Night at the Opera', 1975, id FROM artists WHERE name = 'Queen' LIMIT 1;

-- =========================
-- TRACKS
-- =========================
INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Come Together', 259, id FROM albums WHERE title = 'Abbey Road' LIMIT 1;

INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Something', 183, id FROM albums WHERE title = 'Abbey Road' LIMIT 1;

INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Money', 382, id FROM albums WHERE title = 'The Dark Side of the Moon' LIMIT 1;

INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Time', 413, id FROM albums WHERE title = 'The Dark Side of the Moon' LIMIT 1;

INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Stairway to Heaven', 482, id FROM albums WHERE title = 'Led Zeppelin IV' LIMIT 1;

INSERT INTO tracks (title, duration_sec, album_id)
SELECT 'Bohemian Rhapsody', 355, id FROM albums WHERE title = 'A Night at the Opera' LIMIT 1;

-- =========================
-- TRACK <-> GENRE
-- =========================
INSERT INTO track_genres (track_id, genre_id)
SELECT t.id, g.id FROM tracks t, genres g
WHERE t.title = 'Come Together' AND g.name = 'Rock'
ON CONFLICT (track_id, genre_id) DO NOTHING;

INSERT INTO track_genres (track_id, genre_id)
SELECT t.id, g.id FROM tracks t CROSS JOIN genres g
WHERE t.title = 'Money' AND g.name IN ('Rock', 'Progressive Rock')
ON CONFLICT (track_id, genre_id) DO NOTHING;

INSERT INTO track_genres (track_id, genre_id)
SELECT t.id, g.id FROM tracks t, genres g
WHERE t.title = 'Stairway to Heaven' AND g.name = 'Hard Rock'
ON CONFLICT (track_id, genre_id) DO NOTHING;

INSERT INTO track_genres (track_id, genre_id)
SELECT t.id, g.id FROM tracks t CROSS JOIN genres g
WHERE t.title = 'Bohemian Rhapsody' AND g.name IN ('Rock', 'Pop Rock')
ON CONFLICT (track_id, genre_id) DO NOTHING;

-- =========================
-- PLAYLISTS (user_id nullable after V2)
-- =========================
INSERT INTO playlists (name, user_id) VALUES ('Classic Rock Hits', NULL)
ON CONFLICT (name) DO NOTHING;

INSERT INTO playlist_tracks (playlist_id, track_id)
SELECT p.id, t.id
FROM playlists p, tracks t
WHERE p.name = 'Classic Rock Hits'
  AND t.title IN ('Stairway to Heaven', 'Bohemian Rhapsody', 'Come Together')
ON CONFLICT (playlist_id, track_id) DO NOTHING;