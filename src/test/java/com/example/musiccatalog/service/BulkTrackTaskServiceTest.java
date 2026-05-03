package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.BulkTrackTaskStatusDTO;
import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BulkTrackTaskServiceTest {

    @Mock
    private BulkTrackTaskRegistry taskRegistry;

    @Mock
    private BulkTrackAsyncWorker asyncWorker;

    @InjectMocks
    private BulkTrackTaskService taskService;

    @Test
    void startBulkCreationShouldReturnAcceptedDtoAndTriggerAsyncWorker() {
        Instant createdAt = Instant.parse("2026-04-12T12:00:00Z");
        List<TrackBulkCreateItemDTO> request = List.of(new TrackBulkCreateItemDTO("Battery", 312));
        BulkTrackTaskStatusDTO createdTask = new BulkTrackTaskStatusDTO(
                14L,
                23L,
                AsyncTaskStatus.QUEUED,
                createdAt,
                null,
                null,
                null,
                null
        );
        when(taskRegistry.createTask(23L)).thenReturn(createdTask);

        var accepted = taskService.startBulkCreation(23L, request);

        assertThat(accepted.taskId()).isEqualTo(14L);
        assertThat(accepted.status()).isEqualTo(AsyncTaskStatus.QUEUED);
        assertThat(accepted.createdAt()).isEqualTo(createdAt);
        verify(asyncWorker).createBulkTracksWithDelay(14L, 23L, request);
    }

    @Test
    void startBulkCreationWithoutDelayShouldReturnAcceptedDtoAndTriggerAsyncWorker() {
        Instant createdAt = Instant.parse("2026-04-12T12:00:00Z");
        List<TrackBulkCreateItemDTO> request = List.of(new TrackBulkCreateItemDTO("Battery", 312));
        BulkTrackTaskStatusDTO createdTask = new BulkTrackTaskStatusDTO(
                15L,
                23L,
                AsyncTaskStatus.QUEUED,
                createdAt,
                null,
                null,
                null,
                null
        );
        when(taskRegistry.createTask(23L)).thenReturn(createdTask);

        var accepted = taskService.startBulkCreationWithoutDelay(23L, request);

        assertThat(accepted.taskId()).isEqualTo(15L);
        assertThat(accepted.status()).isEqualTo(AsyncTaskStatus.QUEUED);
        assertThat(accepted.createdAt()).isEqualTo(createdAt);
        verify(asyncWorker).createBulkTracksWithoutDelay(15L, 23L, request);
    }

    @Test
    void getTaskStatusShouldDelegateToRegistry() {
        BulkTrackTaskStatusDTO status = new BulkTrackTaskStatusDTO(
                2L,
                23L,
                AsyncTaskStatus.RUNNING,
                Instant.now(),
                Instant.now(),
                null,
                null,
                null
        );
        when(taskRegistry.getTaskStatus(23L, 2L)).thenReturn(status);

        assertThat(taskService.getTaskStatus(23L, 2L)).isEqualTo(status);
    }
}
