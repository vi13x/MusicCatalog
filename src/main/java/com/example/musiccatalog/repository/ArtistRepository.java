package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    boolean existsByNameIgnoreCase(String name);
}