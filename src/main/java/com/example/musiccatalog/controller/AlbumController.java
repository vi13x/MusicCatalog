package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.service.AlbumService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService service;

    public AlbumController(AlbumService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlbumDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/search/jpql")
    public Page<AlbumDTO> searchWithJpql(@RequestParam(required = false) String title,
                                         @RequestParam(required = false) String artistName,
                                         @RequestParam(required = false) String genreName,
                                         @RequestParam(required = false) Integer yearFrom,
                                         @RequestParam(required = false) Integer yearTo,
                                         @PageableDefault(size = 20) Pageable pageable) {
        return service.searchWithJpql(title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @GetMapping("/search/native")
    public Page<AlbumDTO> searchWithNative(@RequestParam(required = false) String title,
                                           @RequestParam(required = false) String artistName,
                                           @RequestParam(required = false) String genreName,
                                           @RequestParam(required = false) Integer yearFrom,
                                           @RequestParam(required = false) Integer yearTo,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return service.searchWithNative(title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @GetMapping("/{id}")
    public AlbumDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<AlbumDTO> create(@Valid @RequestBody AlbumDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public AlbumDTO update(@PathVariable Long id, @Valid @RequestBody AlbumDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}