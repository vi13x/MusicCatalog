package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.service.AlbumService;
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
@RequestMapping("/api/albums")
public class AlbumCRUD {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<AlbumDTO>> findAll() {
        return ResponseEntity.ok(albumService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AlbumDTO> findById(@PathVariable("id") long id) {
        return ResponseEntity.ok(albumService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<AlbumDTO> create(@RequestBody AlbumDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.create(request));
    }

    @PutMapping("/update")
    public ResponseEntity<AlbumDTO> update(@RequestBody AlbumDTO request) {
        return ResponseEntity.ok(albumService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeById(@PathVariable("id") long id) {
        albumService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}