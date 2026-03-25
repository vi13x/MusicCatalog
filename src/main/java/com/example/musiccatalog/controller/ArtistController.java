package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.service.ArtistService;
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
@RequestMapping("/api/artists")
@Tag(name = "Artists", description = "Operations for managing artists")
public class ArtistController {

    private final ArtistService service;

    public ArtistController(ArtistService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all artists", description = "Returns all artists.")
    public List<ArtistDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get artist by id", description = "Returns a single artist by id.")
    public ArtistDTO getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create artist", description = "Creates a new artist.")
    public ResponseEntity<ArtistDTO> create(@Valid @RequestBody ArtistDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update artist", description = "Updates an existing artist.")
    public ArtistDTO update(@PathVariable @Positive Long id, @Valid @RequestBody ArtistDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete artist", description = "Deletes an artist by id.")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
