package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackCRUD {

    private final TrackService service;

    public TrackCRUD(TrackService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrackDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TrackDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TrackDTO> create(@Valid @RequestBody TrackDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public TrackDTO update(@PathVariable Long id, @Valid @RequestBody TrackDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}