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

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.service.PlaylistService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/playlists")
public class PlaylistCRUD {

    private final PlaylistService playlistService;

    @PostMapping("/create")
    public ResponseEntity<PlaylistDTO> create(@RequestBody PlaylistDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PlaylistDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(playlistService.findById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<PlaylistDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.FOUND).body(playlistService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<PlaylistDTO> update(@RequestBody PlaylistDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(playlistService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeById(@PathVariable Long id) {
        playlistService.removeById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }
}
