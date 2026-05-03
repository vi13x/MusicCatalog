package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.TrackBulkCreateItemDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.TrackRepository;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumSearchIndex albumSearchIndex;

    @InjectMocks
    private TrackService trackService;

    @Test
    void getAllShouldMapTracks() {
        Album album = album(5L);
        Track first = track(1L, "Battery", 312, album);
        Track second = track(2L, "Orion", 507, album);

        when(trackRepository.findAll()).thenReturn(List.of(first, second));

        List<TrackDTO> result = trackService.getAll();

        assertThat(result).extracting(TrackDTO::title).containsExactly("Battery", "Orion");
    }

    @Test
    void getByIdShouldReturnTrack() {
        Album album = album(7L);
        Track track = track(4L, "One", 447, album);

        when(trackRepository.findById(4L)).thenReturn(Optional.of(track));

        TrackDTO result = trackService.getById(4L);

        assertThat(result.id()).isEqualTo(4L);
        assertThat(result.albumId()).isEqualTo(7L);
    }

    @Test
    void createShouldSaveTrackAndClearIndex() {
        Album album = album(7L);
        TrackDTO request = new TrackDTO(null, " Battery ", 312, 7L);

        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Battery")).thenReturn(false);
        when(trackRepository.save(any(Track.class))).thenAnswer(invocation -> {
            Track saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });

        TrackDTO result = trackService.create(request);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.title()).isEqualTo("Battery");
        verify(albumSearchIndex).clear();
    }

    @Test
    void createShouldThrowWhenAlbumIsMissing() {
        when(albumRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.create(new TrackDTO(null, "Battery", 312, 5L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Album not found: 5");

        verifyNoInteractions(albumSearchIndex);
    }

    @Test
    void createShouldRejectDuplicateTrackTitle() {
        Album album = album(7L);
        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Battery")).thenReturn(true);

        assertThatThrownBy(() -> trackService.create(new TrackDTO(null, "Battery", 312, 7L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Track title already exists in album");
    }

    @Test
    void createBulkShouldPersistAllTracksAndTrimTitles() {
        Album album = album(7L);
        List<TrackBulkCreateItemDTO> request = List.of(
                new TrackBulkCreateItemDTO(" Battery ", 312),
                new TrackBulkCreateItemDTO("Orion", 507)
        );

        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Battery")).thenReturn(false);
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Orion")).thenReturn(false);
        when(trackRepository.saveAndFlush(any(Track.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<TrackDTO> result = trackService.createBulk(7L, request);

        assertThat(result).extracting(TrackDTO::title).containsExactly("Battery", "Orion");
        verify(trackRepository, times(2)).saveAndFlush(any(Track.class));
        verify(albumSearchIndex).clear();
    }

    @Test
    void createBulkWithoutTransactionShouldPersistTracks() {
        Album album = album(8L);
        List<TrackBulkCreateItemDTO> request = List.of(new TrackBulkCreateItemDTO("Fade to Black", 417));

        when(albumRepository.findById(8L)).thenReturn(Optional.of(album));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(8L, "Fade to Black")).thenReturn(false);
        when(trackRepository.saveAndFlush(any(Track.class))).thenAnswer(invocation -> {
            Track saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 22L);
            return saved;
        });

        List<TrackDTO> result = trackService.createBulkWithoutTransaction(8L, request);

        assertThat(result).singleElement().extracting(TrackDTO::id).isEqualTo(22L);
        verify(albumSearchIndex).clear();
    }

    @Test
    void createBulkShouldStopOnDuplicateTrackTitleAndStillClearIndex() {
        Album album = album(7L);
        List<TrackBulkCreateItemDTO> request = List.of(
                new TrackBulkCreateItemDTO("Battery", 312),
                new TrackBulkCreateItemDTO("Orion", 507),
                new TrackBulkCreateItemDTO("battery", 312)
        );

        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Battery")).thenReturn(false);
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "Orion")).thenReturn(false);
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCase(7L, "battery")).thenReturn(true);
        when(trackRepository.saveAndFlush(any(Track.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> trackService.createBulk(7L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Track title already exists in album");

        verify(trackRepository, times(2)).saveAndFlush(any(Track.class));
        verify(albumSearchIndex).clear();
    }

    @Test
    void createBulkShouldRejectEmptyRequestAndStillClearIndex() {
        Album album = album(7L);
        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));

        assertThatThrownBy(() -> trackService.createBulk(7L, List.of()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Bulk track request must contain at least one track");

        verify(trackRepository, never()).saveAndFlush(any(Track.class));
        verify(albumSearchIndex).clear();
    }

    @Test
    void createBulkShouldRejectBlankTrackTitleAndStillClearIndex() {
        Album album = album(7L);
        when(albumRepository.findById(7L)).thenReturn(Optional.of(album));

        assertThatThrownBy(() -> trackService.createBulk(7L, List.of(new TrackBulkCreateItemDTO("   ", 312))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Track title must not be blank");

        verify(albumSearchIndex).clear();
    }

    @Test
    void createBulkShouldThrowWhenAlbumIsMissingAndStillClearIndex() {
        when(albumRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.createBulk(77L, List.of(new TrackBulkCreateItemDTO("Battery", 312))))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Album not found: 77");

        verify(albumSearchIndex).clear();
    }

    @Test
    void updateShouldSaveTrackAndClearIndex() {
        Album album = album(3L);
        Track track = track(19L, "Old", 200, album);

        when(trackRepository.findById(19L)).thenReturn(Optional.of(track));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCaseAndIdNot(3L, "The Four Horsemen", 19L)).thenReturn(false);
        when(trackRepository.save(track)).thenReturn(track);

        TrackDTO result = trackService.update(19L, new TrackDTO(null, " The Four Horsemen ", 256, 3L));

        assertThat(result.title()).isEqualTo("The Four Horsemen");
        assertThat(result.durationSec()).isEqualTo(256);
        verify(albumSearchIndex).clear();
    }

    @Test
    void updateShouldRejectDuplicateTrackTitle() {
        Album album = album(3L);
        Track track = track(19L, "Old", 200, album);

        when(trackRepository.findById(19L)).thenReturn(Optional.of(track));
        when(trackRepository.existsByAlbum_IdAndTitleIgnoreCaseAndIdNot(3L, "Battery", 19L)).thenReturn(true);

        assertThatThrownBy(() -> trackService.update(19L, new TrackDTO(null, "Battery", 312, 3L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Track title already exists in album");
    }

    @Test
    void deleteShouldRemoveTrackAndClearIndex() {
        Album album = album(7L);
        Track track = track(9L, "Disposable Heroes", 496, album);

        when(trackRepository.findById(9L)).thenReturn(Optional.of(track));

        trackService.delete(9L);

        verify(trackRepository).delete(track);
        verify(albumSearchIndex).clear();
    }

    @Test
    void getByIdShouldThrowWhenTrackIsMissing() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Track not found: 99");
    }

    @Test
    void bulkMethodsShouldExposeTransactionalDifferenceExplicitly() throws NoSuchMethodException {
        Method transactionalMethod = TrackService.class.getMethod("createBulk", Long.class, List.class);
        Method nonTransactionalMethod = TrackService.class.getMethod("createBulkWithoutTransaction", Long.class, List.class);

        assertThat(transactionalMethod.isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(nonTransactionalMethod.isAnnotationPresent(Transactional.class)).isFalse();
    }

    private Album album(Long id) {
        Album album = new Album();
        ReflectionTestUtils.setField(album, "id", id);
        return album;
    }

    private Track track(Long id, String title, Integer durationSec, Album album) {
        Track track = new Track(title, durationSec);
        ReflectionTestUtils.setField(track, "id", id);
        track.setAlbum(album);
        return track;
    }
}
