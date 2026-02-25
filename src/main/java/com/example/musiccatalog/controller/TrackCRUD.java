package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.service.TrackService;
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
@RequestMapping("/api/tracks")
public class TrackCRUD {

    private final TrackService trackService;

    @PostMapping("/create")
    public ResponseEntity<TrackDTO> create(@RequestBody TrackDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trackService.create(request));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TrackDTO> findById(@PathVariable("id") long id) {
        return ResponseEntity.ok(trackService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TrackDTO>> findAll() {
        return ResponseEntity.ok(trackService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<TrackDTO> update(@RequestBody TrackDTO request) {
        return ResponseEntity.ok(trackService.update(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeById(@PathVariable("id") long id) {
        trackService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}