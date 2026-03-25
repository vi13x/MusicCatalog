package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.service.TrackService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tracks")
@Tag(name = "Tracks", description = "Operations for managing tracks")
public class TrackController {

    private final TrackService service;

    public TrackController(TrackService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all tracks", description = "Returns all tracks.")
    public List<TrackDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get track by id", description = "Returns a single track by id.")
    public TrackDTO getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create track", description = "Creates a new track.")
    public ResponseEntity<TrackDTO> create(@Valid @RequestBody TrackDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update track", description = "Updates an existing track.")
    public TrackDTO update(@PathVariable @Positive Long id, @Valid @RequestBody TrackDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete track", description = "Deletes a track by id.")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
