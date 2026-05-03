package com.example.musiccatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.musiccatalog.dto.AlbumDTO;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class AlbumSearchIndexTest {

    private final AlbumSearchIndex index = new AlbumSearchIndex();

    @Test
    void shouldStoreAndReturnPageByCompositeKey() {
        AlbumSearchCacheKey key = new AlbumSearchCacheKey(
                AlbumSearchMode.JPQL,
                new AlbumSearchCriteria("album", "artist", "rock", 1990, 2000),
                PageRequest.of(0, 20)
        );
        Page<AlbumDTO> page = new PageImpl<>(List.of(new AlbumDTO(1L, "Album", 1999, 2L, Set.of(3L), List.of())));

        index.put(key, page);

        Page<AlbumDTO> cached = index.get(key);
        assertNotNull(cached);
        assertEquals(1, cached.getTotalElements());
        assertEquals("Album", cached.getContent().getFirst().title());
    }

    @Test
    void shouldInvalidateAllCachedEntries() {
        AlbumSearchCacheKey key = new AlbumSearchCacheKey(
                AlbumSearchMode.NATIVE,
                new AlbumSearchCriteria("album", "artist", "rock", null, null),
                PageRequest.of(0, 10)
        );
        index.put(key, new PageImpl<>(List.of()));

        index.clear();

        assertNull(index.get(key));
    }
}