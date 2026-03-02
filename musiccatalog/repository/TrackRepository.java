package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
}