package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.service.PlaylistService;
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
@RequestMapping("/api/playlists")
@Tag(name = "Playlists", description = "Operations for managing playlists")
public class PlaylistController {

    private final PlaylistService service;

    public PlaylistController(PlaylistService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all playlists", description = "Returns all playlists.")
    public List<PlaylistDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get playlist by id", description = "Returns a single playlist by id.")
    public PlaylistDTO getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create playlist", description = "Creates a new playlist.")
    public ResponseEntity<PlaylistDTO> create(@Valid @RequestBody PlaylistDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update playlist", description = "Updates an existing playlist.")
    public PlaylistDTO update(@PathVariable @Positive Long id, @Valid @RequestBody PlaylistDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete playlist", description = "Deletes a playlist by id.")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
