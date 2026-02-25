package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.service.GenreService;
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
@RequestMapping("/api/genres")
public class GenreCRUD {

    private final GenreService genreService;

    @PostMapping("/create")
    public ResponseEntity<GenreDTO> create(@RequestBody GenreDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GenreDTO> findById(@PathVariable("id") long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> findAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<GenreDTO> update(@RequestBody GenreDTO request) {
        return ResponseEntity.ok(genreService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeById(@PathVariable("id") long id) {
        genreService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}