-- V2: Make user_id nullable for playlists (optional user association)
ALTER TABLE playlists ALTER COLUMN user_id DROP NOT NULL;
