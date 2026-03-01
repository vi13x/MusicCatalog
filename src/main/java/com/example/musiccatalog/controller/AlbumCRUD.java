package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumCRUD {

    private final AlbumService service;

    public AlbumCRUD(AlbumService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlbumDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AlbumDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<AlbumDTO> create(@Valid @RequestBody AlbumDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public AlbumDTO update(@PathVariable Long id, @Valid @RequestBody AlbumDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}