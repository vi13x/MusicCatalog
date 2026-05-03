package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.exception.NotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkTrackTaskRegistryTest {

    private final BulkTrackTaskRegistry registry = new BulkTrackTaskRegistry();

    @Test
    void createTaskShouldQueueTaskAndIncrementSubmittedCounter() {
        var firstTask = registry.createTask(23L);
        var secondTask = registry.createTask(24L);

        assertThat(firstTask.taskId()).isEqualTo(1L);
        assertThat(firstTask.albumId()).isEqualTo(23L);
        assertThat(firstTask.status()).isEqualTo(AsyncTaskStatus.QUEUED);
        assertThat(secondTask.taskId()).isEqualTo(2L);

        assertThat(registry.submittedTasks()).isEqualTo(2L);
        assertThat(registry.runningTasks()).isZero();
        assertThat(registry.completedTasks()).isZero();
        assertThat(registry.failedTasks()).isZero();
    }

    @Test
    void markRunningAndCompletedShouldUpdateStatusAndCounters() {
        var created = registry.createTask(23L);
        List<TrackDTO> result = List.of(new TrackDTO(101L, "Battery", 312, 23L));

        registry.markRunning(created.taskId());
        registry.markCompleted(created.taskId(), result);

        var status = registry.getTaskStatus(23L, created.taskId());

        assertThat(status.status()).isEqualTo(AsyncTaskStatus.COMPLETED);
        assertThat(status.startedAt()).isNotNull();
        assertThat(status.finishedAt()).isNotNull();
        assertThat(status.result()).isEqualTo(result);
        assertThat(registry.runningTasks()).isZero();
        assertThat(registry.completedTasks()).isEqualTo(1L);
    }

    @Test
    void markFailedShouldUpdateStatusAndCounters() {
        var created = registry.createTask(23L);

        registry.markRunning(created.taskId());
        registry.markFailed(created.taskId(), "Duplicate");

        var status = registry.getTaskStatus(23L, created.taskId());

        assertThat(status.status()).isEqualTo(AsyncTaskStatus.FAILED);
        assertThat(status.errorMessage()).isEqualTo("Duplicate");
        assertThat(registry.runningTasks()).isZero();
        assertThat(registry.failedTasks()).isEqualTo(1L);
    }

    @Test
    void getTaskStatusShouldThrowWhenAlbumIdDoesNotMatch() {
        var created = registry.createTask(23L);

        assertThatThrownBy(() -> registry.getTaskStatus(24L, created.taskId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Async task not found: " + created.taskId());
    }
}
