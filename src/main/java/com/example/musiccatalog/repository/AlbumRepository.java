package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(
            value = """
                    SELECT DISTINCT a.id
                    FROM Album a
                    JOIN a.artist artist
                    LEFT JOIN a.genres genre
                    WHERE LOWER(a.title) LIKE :titlePattern
                      AND LOWER(artist.name) LIKE :artistNamePattern
                      AND COALESCE(LOWER(genre.name), '') LIKE :genreNamePattern
                      AND (:yearFrom IS NULL OR a.year >= :yearFrom)
                      AND (:yearTo IS NULL OR a.year <= :yearTo)
                    ORDER BY a.id
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT a.id)
                    FROM Album a
                    JOIN a.artist artist
                    LEFT JOIN a.genres genre
                    WHERE LOWER(a.title) LIKE :titlePattern
                      AND LOWER(artist.name) LIKE :artistNamePattern
                      AND COALESCE(LOWER(genre.name), '') LIKE :genreNamePattern
                      AND (:yearFrom IS NULL OR a.year >= :yearFrom)
                      AND (:yearTo IS NULL OR a.year <= :yearTo)
                    """
    )
    Page<Long> searchIdsByFiltersJpql(@Param("titlePattern") String titlePattern,
                                      @Param("artistNamePattern") String artistNamePattern,
                                      @Param("genreNamePattern") String genreNamePattern,
                                      @Param("yearFrom") Integer yearFrom,
                                      @Param("yearTo") Integer yearTo,
                                      Pageable pageable);

    @Query(
            value = """
                    SELECT DISTINCT a.id
                    FROM albums a
                    JOIN artists art ON art.id = a.artist_id
                    LEFT JOIN album_genres ag ON ag.album_id = a.id
                    LEFT JOIN genres g ON g.id = ag.genre_id
                    WHERE LOWER(a.title) LIKE :titlePattern
                      AND LOWER(art.name) LIKE :artistNamePattern
                      AND COALESCE(LOWER(g.name), '') LIKE :genreNamePattern
                      AND (:yearFrom IS NULL OR a.year >= :yearFrom)
                      AND (:yearTo IS NULL OR a.year <= :yearTo)
                    ORDER BY a.id
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT a.id)
                    FROM albums a
                    JOIN artists art ON art.id = a.artist_id
                    LEFT JOIN album_genres ag ON ag.album_id = a.id
                    LEFT JOIN genres g ON g.id = ag.genre_id
                    WHERE LOWER(a.title) LIKE :titlePattern
                      AND LOWER(art.name) LIKE :artistNamePattern
                      AND COALESCE(LOWER(g.name), '') LIKE :genreNamePattern
                      AND (:yearFrom IS NULL OR a.year >= :yearFrom)
                      AND (:yearTo IS NULL OR a.year <= :yearTo)
                    """,
            nativeQuery = true
    )
    Page<Long> searchIdsByFiltersNative(@Param("titlePattern") String titlePattern,
                                        @Param("artistNamePattern") String artistNamePattern,
                                        @Param("genreNamePattern") String genreNamePattern,
                                        @Param("yearFrom") Integer yearFrom,
                                        @Param("yearTo") Integer yearTo,
                                        Pageable pageable);
}