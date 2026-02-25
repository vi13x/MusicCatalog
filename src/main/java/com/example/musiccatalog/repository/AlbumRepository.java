package com.example.musiccatalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.musiccatalog.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @EntityGraph(attributePaths = {"artist", "tracks"})
    Optional<Album> findWithArtistAndTracksById(Long id);

    @EntityGraph(attributePaths = {"artist", "tracks"})
    List<Album> findAll();
}
