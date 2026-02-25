package com.example.musiccatalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tracks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // В БД title, скорее всего, NOT NULL (сделаем так же)
    @Column(name = "title", nullable = false)
    private String title;

    // ВАЖНО: в БД колонка duration_sec, а не duration_seconds
    @Column(name = "duration_sec", nullable = false)
    private Integer durationSec;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    @JsonIgnoreProperties({"tracks", "artist"})
    private Album album;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_genres",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @JsonIgnoreProperties({"tracks"})
    private Set<Genre> genres = new HashSet<>();
}