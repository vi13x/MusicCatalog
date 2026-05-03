package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AlbumDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

@Service
public class AlbumSearchIndex {

    private final Map<AlbumSearchCacheKey, Page<AlbumDTO>> cache = new HashMap<>();

    public synchronized Page<AlbumDTO> get(AlbumSearchCacheKey key) {
        return cache.get(key);
    }

    public synchronized void put(AlbumSearchCacheKey key, Page<AlbumDTO> page) {
        cache.put(key, copy(page));
    }

    public synchronized void clear() {
        cache.clear();
    }

    private Page<AlbumDTO> copy(Page<AlbumDTO> page) {
        return new PageImpl<>(List.copyOf(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}