package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.BulkTrackTaskStatusDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class BulkTrackTaskRegistry {

    private final AtomicLong taskSequence = new AtomicLong();
    private final AtomicLong submittedTasks = new AtomicLong();
    private final AtomicInteger runningTasks = new AtomicInteger();
    private final AtomicLong completedTasks = new AtomicLong();
    private final AtomicLong failedTasks = new AtomicLong();
    private final ConcurrentMap<Long, TaskState> tasks = new ConcurrentHashMap<>();

    public BulkTrackTaskStatusDTO createTask(Long albumId) {
        long taskId = taskSequence.incrementAndGet();
        Instant createdAt = Instant.now();
        TaskState state = new TaskState(taskId, albumId, AsyncTaskStatus.QUEUED, createdAt, null, null, null, null);
        tasks.put(taskId, state);
        submittedTasks.incrementAndGet();
        return toDto(state);
    }

    public void markRunning(Long taskId) {
        updateTask(taskId, state -> {
            runningTasks.incrementAndGet();
            return new TaskState(
                    state.taskId(),
                    state.albumId(),
                    AsyncTaskStatus.RUNNING,
                    state.createdAt(),
                    Instant.now(),
                    null,
                    null,
                    null
            );
        });
    }

    public void markCompleted(Long taskId, List<TrackDTO> result) {
        updateTask(taskId, state -> {
            runningTasks.decrementAndGet();
            completedTasks.incrementAndGet();
            return new TaskState(
                    state.taskId(),
                    state.albumId(),
                    AsyncTaskStatus.COMPLETED,
                    state.createdAt(),
                    state.startedAt(),
                    Instant.now(),
                    List.copyOf(result),
                    null
            );
        });
    }

    public void markFailed(Long taskId, String errorMessage) {
        updateTask(taskId, state -> {
            runningTasks.decrementAndGet();
            failedTasks.incrementAndGet();
            return new TaskState(
                    state.taskId(),
                    state.albumId(),
                    AsyncTaskStatus.FAILED,
                    state.createdAt(),
                    state.startedAt(),
                    Instant.now(),
                    null,
                    errorMessage
            );
        });
    }

    public BulkTrackTaskStatusDTO getTaskStatus(Long albumId, Long taskId) {
        TaskState state = getRequiredTask(taskId);
        if (!state.albumId().equals(albumId)) {
            throw new NotFoundException(ErrorMessages.ASYNC_TASK_NOT_FOUND + taskId);
        }
        return toDto(state);
    }

    long submittedTasks() {
        return submittedTasks.get();
    }

    int runningTasks() {
        return runningTasks.get();
    }

    long completedTasks() {
        return completedTasks.get();
    }

    long failedTasks() {
        return failedTasks.get();
    }

    private void updateTask(Long taskId, java.util.function.Function<TaskState, TaskState> updater) {
        TaskState updated = tasks.compute(taskId, (id, currentState) -> {
            if (currentState == null) {
                throw new NotFoundException(ErrorMessages.ASYNC_TASK_NOT_FOUND + taskId);
            }
            return updater.apply(currentState);
        });
        if (updated == null) {
            throw new NotFoundException(ErrorMessages.ASYNC_TASK_NOT_FOUND + taskId);
        }
    }

    private TaskState getRequiredTask(Long taskId) {
        TaskState state = tasks.get(taskId);
        if (state == null) {
            throw new NotFoundException(ErrorMessages.ASYNC_TASK_NOT_FOUND + taskId);
        }
        return state;
    }

    private BulkTrackTaskStatusDTO toDto(TaskState state) {
        return new BulkTrackTaskStatusDTO(
                state.taskId(),
                state.albumId(),
                state.status(),
                state.createdAt(),
                state.startedAt(),
                state.finishedAt(),
                state.result(),
                state.errorMessage()
        );
    }

    private record TaskState(
            Long taskId,
            Long albumId,
            AsyncTaskStatus status,
            Instant createdAt,
            Instant startedAt,
            Instant finishedAt,
            List<TrackDTO> result,
            String errorMessage
    ) {
    }
}
