package com.example.musiccatalog.controller;

import java.util.List;

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

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.service.GenreService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreCRUD {

    private final GenreService genreService;

    @PostMapping("/create")
    public ResponseEntity<GenreDTO> create(@RequestBody GenreDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GenreDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(genreService.findById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<GenreDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.FOUND).body(genreService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<GenreDTO> update(@RequestBody GenreDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeById(@PathVariable Long id) {
        genreService.removeById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }
}
