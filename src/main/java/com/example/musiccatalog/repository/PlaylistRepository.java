package com.example.musiccatalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.musiccatalog.entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @EntityGraph(attributePaths = {"tracks", "tracks.album", "tracks.genres"})
    Optional<Playlist> findWithTracksById(Long id);

    @EntityGraph(attributePaths = {"tracks"})
    List<Playlist> findAll();
}
