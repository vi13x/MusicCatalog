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

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.service.AlbumService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumCRUD {

    private final AlbumService albumService;

    @PostMapping("/create")
    public ResponseEntity<AlbumDTO> create(@RequestBody AlbumDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AlbumDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(albumService.findById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<AlbumDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.FOUND).body(albumService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<AlbumDTO> update(@RequestBody AlbumDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(albumService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeById(@PathVariable Long id) {
        albumService.removeById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }
}
