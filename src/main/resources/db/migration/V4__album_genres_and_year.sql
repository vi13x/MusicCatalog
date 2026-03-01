-- V4: переход с track_genres на album_genres и release_year -> year (если БД создана старыми миграциями)

-- Переименовать колонку year в albums (если была release_year)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'albums' AND column_name = 'release_year'
  ) THEN
    ALTER TABLE albums RENAME COLUMN release_year TO year;
  END IF;
END $$;

-- Заменить track_genres на album_genres
CREATE TABLE IF NOT EXISTS album_genres (
    album_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, genre_id),
    CONSTRAINT fk_album_genres_album
    FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
    CONSTRAINT fk_album_genres_genre
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_album_genres_genre_id ON album_genres(genre_id);

-- Перенос данных из track_genres в album_genres (только если старая таблица есть)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'track_genres') THEN
    INSERT INTO album_genres (album_id, genre_id)
    SELECT t.album_id, tg.genre_id FROM track_genres tg
    JOIN tracks t ON t.id = tg.track_id
    ON CONFLICT (album_id, genre_id) DO NOTHING;
    DROP TABLE track_genres;
  END IF;
END $$;
