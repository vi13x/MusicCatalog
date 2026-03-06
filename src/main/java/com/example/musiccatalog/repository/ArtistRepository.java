package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.albums WHERE a.id = :id")
    Optional<Artist> findByIdWithAlbums(@Param("id") Long id);
}