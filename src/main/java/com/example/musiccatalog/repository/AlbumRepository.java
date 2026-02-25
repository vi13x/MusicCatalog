package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @EntityGraph(attributePaths = {"artist", "tracks", "tracks.genres"})
    Optional<Album> findWithArtistAndTracksById(Long id);

    @Override
    @EntityGraph(attributePaths = {"artist", "tracks", "tracks.genres"})
    List<Album> findAll();
}