package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Override
    @EntityGraph(attributePaths = {"artist", "tracks", "genres"})
    List<Album> findAll();

    @EntityGraph(attributePaths = {"artist", "tracks", "genres"})
    Optional<Album> findWithAllById(Long id);


    @EntityGraph(attributePaths = {"artist", "tracks", "genres"})
    List<Album> findAllByIdIn(Iterable<Long> ids);

    @Query("SELECT a FROM Album a WHERE a.id IN :ids")
    @EntityGraph(attributePaths = {"artist", "tracks"})
    List<Album> findAllByIdInWithTracks(@Param("ids") Iterable<Long> ids);
}