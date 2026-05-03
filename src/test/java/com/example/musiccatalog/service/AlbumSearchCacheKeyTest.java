package com.example.musiccatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

class AlbumSearchCacheKeyTest {

    @Test
    void shouldTreatIdenticalParametersAsSameKey() {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria("album", "artist", "rock", 1990, 2000);
        AlbumSearchCacheKey left = new AlbumSearchCacheKey(AlbumSearchMode.JPQL, criteria, PageRequest.of(0, 20));
        AlbumSearchCacheKey right = new AlbumSearchCacheKey(AlbumSearchMode.JPQL, criteria, PageRequest.of(0, 20));

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    @Test
    void shouldDifferentiateKeysByQueryParameters() {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria("album", "artist", "rock", 1990, 2000);
        AlbumSearchCacheKey left = new AlbumSearchCacheKey(AlbumSearchMode.JPQL, criteria, PageRequest.of(0, 20));
        AlbumSearchCacheKey right = new AlbumSearchCacheKey(AlbumSearchMode.NATIVE, criteria, PageRequest.of(1, 20));

        assertNotEquals(left, right);
    }

    @Test
    void shouldBeEqualToItselfAndRejectOtherTypes() {
        AlbumSearchCacheKey key = new AlbumSearchCacheKey(
                AlbumSearchMode.JPQL,
                new AlbumSearchCriteria("album", "artist", "rock", 1990, 2000),
                PageRequest.of(0, 20)
        );

        assertTrue(key.equals(key));
        assertNotEquals(key, "not-a-key");
    }
}
