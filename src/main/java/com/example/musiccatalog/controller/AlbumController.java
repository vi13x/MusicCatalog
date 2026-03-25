package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;

@Validated
@RestController
@RequestMapping("/api/albums")
@Tag(name = "Albums", description = "Operations for managing albums and album search")
public class AlbumController {

    private final AlbumService service;

    public AlbumController(AlbumService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all albums", description = "Returns all albums with related artist, tracks and genres.")
    public List<AlbumDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Search albums with JPQL", description = "Searches albums by filters using JPQL query.")
    public Page<AlbumDTO> searchWithJpql(@RequestParam(required = false) @Size(max = 160) String title,
                                         @RequestParam(required = false) @Size(max = 120) String artistName,
                                         @RequestParam(required = false) @Size(max = 80) String genreName,
                                         @RequestParam(required = false) @Min(1900) @Max(2100) Integer yearFrom,
                                         @RequestParam(required = false) @Min(1900) @Max(2100) Integer yearTo,
                                         @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return service.searchWithJpql(title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @GetMapping("/search/native")
    @Operation(summary = "Search albums with native SQL", description = "Searches albums by filters using native SQL query.")
    public Page<AlbumDTO> searchWithNative(@RequestParam(required = false) @Size(max = 160) String title,
                                           @RequestParam(required = false) @Size(max = 120) String artistName,
                                           @RequestParam(required = false) @Size(max = 80) String genreName,
                                           @RequestParam(required = false) @Min(1900) @Max(2100) Integer yearFrom,
                                           @RequestParam(required = false) @Min(1900) @Max(2100) Integer yearTo,
                                           @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return service.searchWithNative(title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get album by id", description = "Returns a single album by its identifier.")
    public AlbumDTO getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create album", description = "Creates a new album with artist, tracks and genres.")
    public ResponseEntity<AlbumDTO> create(@Valid @RequestBody AlbumDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update album", description = "Updates an existing album by id.")
    public AlbumDTO update(@PathVariable @Positive Long id, @Valid @RequestBody AlbumDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete album", description = "Deletes an album by id.")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
