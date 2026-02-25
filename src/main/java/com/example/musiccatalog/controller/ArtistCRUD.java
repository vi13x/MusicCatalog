package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.service.ArtistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/artists")
public class ArtistCRUD {

    private final ArtistService artistService;

    @PostMapping("/create")
    public ResponseEntity<ArtistDTO> create(@RequestBody ArtistDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ArtistDTO> findById(@PathVariable("id") long id) {
        return ResponseEntity.ok(artistService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> findAll() {
        return ResponseEntity.ok(artistService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<ArtistDTO> update(@RequestBody ArtistDTO request) {
        return ResponseEntity.ok(artistService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeById(@PathVariable("id") long id) {
        artistService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}