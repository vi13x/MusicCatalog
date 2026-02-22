package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.Album;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AlbumRepository {

    private final Map<Long, Album> storage = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Album> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Album> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Album> findByTitle(String title) {
        return storage.values().stream()
                .filter(a -> a.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public Album save(Album album) {
        if (album.getId() == null) {
            album.setId(idGenerator.getAndIncrement());
        }
        storage.put(album.getId(), album);
        return album;
    }

    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}