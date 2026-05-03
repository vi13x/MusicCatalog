package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.PlaylistDTO;
import com.example.musiccatalog.entity.Playlist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.PlaylistRepository;
import com.example.musiccatalog.repository.TrackRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @Test
    void getAllShouldMapPlaylists() {
        Playlist first = playlist(1L, "Road Trip", Set.of(track(11L), track(12L)));
        Playlist second = playlist(2L, "Focus", Set.of());

        when(playlistRepository.findAll()).thenReturn(List.of(first, second));

        assertThat(playlistService.getAll()).extracting(PlaylistDTO::name).containsExactly("Road Trip", "Focus");
    }

    @Test
    void getByIdShouldReturnPlaylist() {
        Playlist playlist = playlist(3L, "Workout", Set.of(track(44L)));
        when(playlistRepository.findById(3L)).thenReturn(Optional.of(playlist));

        PlaylistDTO result = playlistService.getById(3L);

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.trackIds()).containsExactly(44L);
    }

    @Test
    void createShouldResolveTrackIdsAndReturnSavedPlaylist() {
        Track firstTrack = track(1L);
        Track secondTrack = track(2L);
        PlaylistDTO request = new PlaylistDTO(null, "Road Trip", Set.of(1L, 2L));

        when(trackRepository.findById(1L)).thenReturn(Optional.of(firstTrack));
        when(trackRepository.findById(2L)).thenReturn(Optional.of(secondTrack));
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlaylistDTO result = playlistService.create(request);

        assertThat(result.name()).isEqualTo("Road Trip");
        assertThat(result.trackIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void createShouldAllowNullTrackIds() {
        PlaylistDTO request = new PlaylistDTO(null, "Focus", null);
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlaylistDTO result = playlistService.create(request);

        assertThat(result.trackIds()).isEmpty();
        verifyNoInteractions(trackRepository);
    }

    @Test
    void updateShouldReplaceTrackIds() {
        Playlist playlist = playlist(9L, "Road Trip", Set.of(track(1L)));
        Track updatedTrack = track(7L);

        when(playlistRepository.findById(9L)).thenReturn(Optional.of(playlist));
        when(trackRepository.findById(7L)).thenReturn(Optional.of(updatedTrack));
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        PlaylistDTO result = playlistService.update(9L, new PlaylistDTO(null, "Updated", Set.of(7L)));

        assertThat(result.name()).isEqualTo("Updated");
        assertThat(result.trackIds()).containsExactly(7L);
    }

    @Test
    void updateShouldThrowWhenTrackIsMissing() {
        Playlist playlist = new Playlist("Road Trip");
        ReflectionTestUtils.setField(playlist, "id", 9L);

        when(playlistRepository.findById(9L)).thenReturn(Optional.of(playlist));
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.update(9L, new PlaylistDTO(null, "Updated", Set.of(99L))))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Track not found: 99");
    }

    @Test
    void deleteShouldRemovePlaylist() {
        Playlist playlist = playlist(13L, "Road Trip", Set.of());
        when(playlistRepository.findById(13L)).thenReturn(Optional.of(playlist));

        playlistService.delete(13L);

        verify(playlistRepository).delete(playlist);
    }

    @Test
    void getByIdShouldThrowWhenPlaylistIsMissing() {
        when(playlistRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.getById(77L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Playlist not found: 77");
    }

    private Playlist playlist(Long id, String name, Set<Track> tracks) {
        Playlist playlist = new Playlist(name);
        ReflectionTestUtils.setField(playlist, "id", id);
        playlist.setTracks(new java.util.HashSet<>(tracks));
        return playlist;
    }

    private Track track(Long id) {
        Track track = new Track();
        ReflectionTestUtils.setField(track, "id", id);
        return track;
    }
}
