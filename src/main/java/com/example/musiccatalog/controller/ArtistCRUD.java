package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistCRUD {

    private final ArtistService service;

    public ArtistCRUD(ArtistService service) {
        this.service = service;
    }

    @GetMapping
    public List<ArtistDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ArtistDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ArtistDTO> create(@Valid @RequestBody ArtistDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ArtistDTO update(@PathVariable Long id, @Valid @RequestBody ArtistDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}