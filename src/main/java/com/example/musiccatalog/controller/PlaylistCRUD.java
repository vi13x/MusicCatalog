package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistCRUD {

    private final PlaylistService service;

    public PlaylistCRUD(PlaylistService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlaylistDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PlaylistDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> create(@Valid @RequestBody PlaylistDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public PlaylistDTO update(@PathVariable Long id, @Valid @RequestBody PlaylistDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}