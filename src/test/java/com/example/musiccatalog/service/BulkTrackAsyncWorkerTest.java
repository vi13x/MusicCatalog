package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.exception.BadRequestException;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BulkTrackAsyncWorkerTest {

    @Mock
    private TrackService trackService;

    @Mock
    private BulkTrackTaskRegistry taskRegistry;

    @InjectMocks
    private BulkTrackAsyncWorker worker;

    @Test
    void createBulkTracksShouldMarkTaskCompleted() throws Exception {
        List<TrackBulkCreateItemDTO> request = List.of(new TrackBulkCreateItemDTO("Battery", 312));
        List<TrackDTO> result = List.of(new TrackDTO(101L, "Battery", 312, 23L));
        when(trackService.createBulk(23L, request)).thenReturn(result);

        List<TrackDTO> actual = worker.createBulkTracksWithoutDelay(5L, 23L, request).get();

        assertThat(actual).isEqualTo(result);
        verify(taskRegistry).markRunning(5L);
        verify(taskRegistry).markCompleted(5L, result);
        verify(taskRegistry, never()).markFailed(eq(5L), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void createBulkTracksShouldMarkTaskFailed() {
        List<TrackBulkCreateItemDTO> request = List.of(new TrackBulkCreateItemDTO("Battery", 312));
        when(trackService.createBulk(23L, request)).thenThrow(new BadRequestException("Duplicate"));

        Future<List<TrackDTO>> future = worker.createBulkTracksWithoutDelay(7L, 23L, request);

        assertThatThrownBy(future::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(BadRequestException.class)
                .hasRootCauseMessage("Duplicate");

        verify(taskRegistry).markRunning(7L);
        verify(taskRegistry).markFailed(7L, "Duplicate");
        verify(taskRegistry, never()).markCompleted(eq(7L), anyList());
    }
}
