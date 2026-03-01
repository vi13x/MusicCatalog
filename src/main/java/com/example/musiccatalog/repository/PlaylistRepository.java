package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}