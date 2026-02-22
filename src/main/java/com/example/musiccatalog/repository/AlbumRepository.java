package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByTitle(String title);
}
