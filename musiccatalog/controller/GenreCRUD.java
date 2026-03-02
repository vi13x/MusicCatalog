package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreCRUD {

    private final GenreService service;

    public GenreCRUD(GenreService service) {
        this.service = service;
    }

    @GetMapping
    public List<GenreDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public GenreDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<GenreDTO> create(@Valid @RequestBody GenreDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public GenreDTO update(@PathVariable Long id, @Valid @RequestBody GenreDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}