package com.example.musiccatalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "year")
    private Integer year;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    @JsonIgnoreProperties({"albums"})
    private Artist artist;


    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"album"})
    @Builder.Default
    private List<Track> tracks = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "album_genres",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @JsonIgnoreProperties({"albums"})
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();


    public Album(String title, Integer year, Artist artist) {
        this.id = null;
        this.title = title;
        this.year = year;
        this.artist = artist;
        this.tracks = new ArrayList<>();
        this.genres = new HashSet<>();
    }

    public void addTrack(Track track) {
        if (track == null) {
            return;
        }
        this.tracks.add(track);
        track.setAlbum(this);
    }

    public void addGenre(Genre genre) {
        if (genre == null) {
            return;
        }
        this.genres.add(genre);
        genre.getAlbums().add(this);
    }

    public void clearGenres() {
        for (Genre g : this.genres) {
            g.getAlbums().remove(this);
        }
        this.genres.clear();
    }
}