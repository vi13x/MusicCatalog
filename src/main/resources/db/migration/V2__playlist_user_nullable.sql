-- V2__playlist_user_nullable.sql
-- Make user_id nullable for playlists (optional user association)

ALTER TABLE playlists
    ALTER COLUMN user_id DROP NOT NULL;