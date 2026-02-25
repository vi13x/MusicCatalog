package com.example.musiccatalog.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "service", "Music Catalog API",
                "status", "OK",
                "albums", "/api/albums",
                "artists", "/api/artists",
                "tracks", "/api/tracks",
                "genres", "/api/genres",
                "playlists", "/api/playlists"
        );
    }
}