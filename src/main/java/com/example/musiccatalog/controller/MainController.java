package com.example.musiccatalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.service.AlbumService;
import com.example.musiccatalog.service.ArtistService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class MainController {

    private final ArtistService artistService;
    private final AlbumService albumService;

    @GetMapping("/artist/name")
    public ResponseEntity<ArtistDTO> getArtist(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.FOUND).body(artistService.findByName(name));
    }

    @GetMapping("/album/id/{id}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(albumService.findById(id));
    }
}
