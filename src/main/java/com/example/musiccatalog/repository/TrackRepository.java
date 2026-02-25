package com.example.musiccatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.musiccatalog.entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
}
