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

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.service.TrackService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/tracks")
public class TrackCRUD {

    private final TrackService trackService;

    @PostMapping("/create")
    public ResponseEntity<TrackDTO> create(@RequestBody TrackDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trackService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TrackDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(trackService.findById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<TrackDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.FOUND).body(trackService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<TrackDTO> update(@RequestBody TrackDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(trackService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeById(@PathVariable Long id) {
        trackService.removeById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }
}
