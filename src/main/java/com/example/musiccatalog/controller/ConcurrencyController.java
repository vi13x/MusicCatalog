package com.example.musiccatalog.controller;

import com.example.musiccatalog.dto.RaceConditionDemoRequestDTO;
import com.example.musiccatalog.dto.RaceConditionRunResultDTO;
import com.example.musiccatalog.service.ConcurrencyDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/concurrency/race-condition")
@Tag(name = "Concurrency", description = "Race condition demo endpoints with unsafe and safe counters")
public class ConcurrencyController {

    private final ConcurrencyDemoService concurrencyDemoService;

    public ConcurrencyController(ConcurrencyDemoService concurrencyDemoService) {
        this.concurrencyDemoService = concurrencyDemoService;
    }

    @PostMapping("/unsafe")
    @Operation(
            summary = "Run unsafe race condition demo",
            description = "Runs the non-thread-safe counter with ExecutorService and 50+ threads to demonstrate lost updates."
    )
    public RaceConditionRunResultDTO runUnsafe(@Valid @RequestBody RaceConditionDemoRequestDTO request) {
        return RaceConditionRunResultDTO.from(
                "unsafe",
                request.threads(),
                request.incrementsPerThread(),
                concurrencyDemoService.runUnsafeCounter(request.threads(), request.incrementsPerThread())
        );
    }

    @PostMapping("/safe")
    @Operation(
            summary = "Run safe race condition demo",
            description = "Runs the thread-safe AtomicLong counter with ExecutorService and the same load to demonstrate the fix."
    )
    public RaceConditionRunResultDTO runSafe(@Valid @RequestBody RaceConditionDemoRequestDTO request) {
        return RaceConditionRunResultDTO.from(
                "safe",
                request.threads(),
                request.incrementsPerThread(),
                concurrencyDemoService.runSafeCounter(request.threads(), request.incrementsPerThread())
        );
    }
}
