package com.example.musiccatalog.controller;

import com.example.musiccatalog.service.DemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
public class MainController {

    private final DemoService demoService;

    public MainController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping("/nplus1")
    public ResponseEntity<String> nPlusOne(@RequestBody List<Long> albumIds) {
        int tracks = demoService.nPlusOneDemo(albumIds);
        return ResponseEntity.ok("Total tracks loaded (N+1 triggered): " + tracks);
    }

    @PostMapping("/nplus1-fixed")
    public ResponseEntity<String> nPlusOneFixed(@RequestBody List<Long> albumIds) {
        int tracks = demoService.nPlusOneFixed(albumIds);
        return ResponseEntity.ok("Total tracks loaded (EntityGraph, no N+1): " + tracks);
    }

    @GetMapping("/no-tx")
    public ResponseEntity<String> noTx(@RequestParam(defaultValue = "NoTxArtist") String artist) {
        demoService.createAlbumWithTracksNoTx(artist);
        return ResponseEntity.ok("Should not reach here (expected error).");
    }

    @GetMapping("/tx")
    public ResponseEntity<String> tx(@RequestParam(defaultValue = "TxArtist") String artist) {
        demoService.createAlbumWithTracksTx(artist);
        return ResponseEntity.ok("Should not reach here (expected error).");
    }
}