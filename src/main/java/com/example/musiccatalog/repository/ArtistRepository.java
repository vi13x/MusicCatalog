package com.example.musiccatalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.musiccatalog.entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"albums"})
    List<Artist> findAll();
}
