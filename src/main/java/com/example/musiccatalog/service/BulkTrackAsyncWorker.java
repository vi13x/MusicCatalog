package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import com.example.musiccatalog.dto.TrackDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BulkTrackAsyncWorker {

    private final TrackService trackService;
    private final BulkTrackTaskRegistry taskRegistry;

    @Value("${app.async.bulk-track-delay-ms:10000}")
    private long bulkTrackDelayMs;

    public BulkTrackAsyncWorker(TrackService trackService, BulkTrackTaskRegistry taskRegistry) {
        this.trackService = trackService;
        this.taskRegistry = taskRegistry;
    }

    @Async("bulkTrackTaskExecutor")
    public Future<List<TrackDTO>> createBulkTracksWithDelay(Long taskId,
                                                            Long albumId,
                                                            List<TrackBulkCreateItemDTO> request) {
        return executeBulkTracks(taskId, albumId, request, true);
    }

    @Async("bulkTrackTaskExecutor")
    public Future<List<TrackDTO>> createBulkTracksWithoutDelay(Long taskId,
                                                               Long albumId,
                                                               List<TrackBulkCreateItemDTO> request) {
        return executeBulkTracks(taskId, albumId, request, false);
    }

    private CompletableFuture<List<TrackDTO>> executeBulkTracks(Long taskId,
                                                                Long albumId,
                                                                List<TrackBulkCreateItemDTO> request,
                                                                boolean withDelay) {
        taskRegistry.markRunning(taskId);
        try {
            if (withDelay) {
                pauseBeforeProcessing();
            }
            List<TrackDTO> result = trackService.createBulk(albumId, request);
            taskRegistry.markCompleted(taskId, result);
            return CompletableFuture.completedFuture(result);
        } catch (RuntimeException ex) {
            taskRegistry.markFailed(taskId, ex.getMessage());
            CompletableFuture<List<TrackDTO>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(ex);
            return failedFuture;
        }
    }

    private void pauseBeforeProcessing() {
        if (bulkTrackDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(bulkTrackDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Async bulk track task interrupted", ex);
        }
    }
}
