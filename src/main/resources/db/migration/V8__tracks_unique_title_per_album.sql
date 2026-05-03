CREATE UNIQUE INDEX IF NOT EXISTS uk_tracks_album_title_ci
    ON tracks (album_id, LOWER(title));
