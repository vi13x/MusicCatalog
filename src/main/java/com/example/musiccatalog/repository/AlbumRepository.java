package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {


    @EntityGraph(attributePaths = {"artist", "tracks", "genres"})
    Optional<Album> findWithAllById(Long id);


    @EntityGraph(attributePaths = {"artist", "tracks", "genres"})
    List<Album> findAllByIdIn(Iterable<Long> ids);
}