package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AsyncTaskAcceptedDTO;
import com.example.musiccatalog.dto.BulkTrackTaskStatusDTO;
import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BulkTrackTaskService {

    private final BulkTrackTaskRegistry taskRegistry;
    private final BulkTrackAsyncWorker asyncWorker;

    public BulkTrackTaskService(BulkTrackTaskRegistry taskRegistry,
                                BulkTrackAsyncWorker asyncWorker) {
        this.taskRegistry = taskRegistry;
        this.asyncWorker = asyncWorker;
    }

    public AsyncTaskAcceptedDTO startBulkCreation(Long albumId, List<TrackBulkCreateItemDTO> request) {
        BulkTrackTaskStatusDTO task = taskRegistry.createTask(albumId);
        asyncWorker.createBulkTracksWithDelay(task.taskId(), albumId, List.copyOf(request));
        return new AsyncTaskAcceptedDTO(task.taskId(), task.status(), task.createdAt());
    }

    public AsyncTaskAcceptedDTO startBulkCreationWithoutDelay(Long albumId, List<TrackBulkCreateItemDTO> request) {
        BulkTrackTaskStatusDTO task = taskRegistry.createTask(albumId);
        asyncWorker.createBulkTracksWithoutDelay(task.taskId(), albumId, List.copyOf(request));
        return new AsyncTaskAcceptedDTO(task.taskId(), task.status(), task.createdAt());
    }

    public BulkTrackTaskStatusDTO getTaskStatus(Long albumId, Long taskId) {
        return taskRegistry.getTaskStatus(albumId, taskId);
    }
}
