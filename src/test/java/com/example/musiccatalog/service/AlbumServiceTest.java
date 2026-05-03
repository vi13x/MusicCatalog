package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;
import com.example.musiccatalog.repository.GenreRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AlbumSearchIndex albumSearchIndex;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void getAllShouldMapAlbums() {
        Album firstAlbum = album(1L, "Ride the Lightning", 1984, 10L);
        Album secondAlbum = album(2L, "Master of Puppets", 1986, 11L);

        when(albumRepository.findAll()).thenReturn(List.of(firstAlbum, secondAlbum));

        List<AlbumDTO> result = albumService.getAll();

        assertThat(result).extracting(AlbumDTO::title).containsExactly("Ride the Lightning", "Master of Puppets");
    }

    @Test
    void getByIdShouldReturnAlbumWithoutDuplicatedTracks() {
        Album album = album(15L, "Black Album", 1991, 4L);
        Genre firstGenre = genre(1L, "Rock");
        Genre secondGenre = genre(2L, "Alternative");
        album.getGenres().add(firstGenre);
        album.getGenres().add(secondGenre);

        Track firstTrack = track(101L, "Track A", 245, album);
        Track duplicateFirstTrack = track(101L, "Track A", 245, album);
        Track secondTrack = track(102L, "Track B", 198, album);
        Track duplicateSecondTrack = track(102L, "Track B", 198, album);
        album.setTracks(new ArrayList<>(List.of(firstTrack, duplicateFirstTrack, secondTrack, duplicateSecondTrack)));

        when(albumRepository.findWithAllById(15L)).thenReturn(Optional.of(album));

        AlbumDTO result = albumService.getById(15L);

        assertThat(result.id()).isEqualTo(15L);
        assertThat(result.title()).isEqualTo("Black Album");
        assertThat(result.tracks()).extracting(TrackDTO::id).containsExactly(101L, 102L);
    }

    @Test
    void createShouldApplyGenresAndTracksAndClearSearchIndex() {
        Artist artist = artist(3L, "Metallica");

        Genre metal = genre(11L, "Metal");
        Genre thrash = genre(12L, "Thrash Metal");

        AlbumDTO request = new AlbumDTO(
                null,
                "Master of Puppets",
                1986,
                3L,
                Set.of(11L, 12L),
                List.of(
                        new TrackDTO(null, "Battery", 312, 3L),
                        new TrackDTO(null, "Orion", 507, 3L)
                )
        );

        when(artistRepository.findById(3L)).thenReturn(Optional.of(artist));
        when(genreRepository.findById(11L)).thenReturn(Optional.of(metal));
        when(genreRepository.findById(12L)).thenReturn(Optional.of(thrash));
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> {
            Album saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 41L);
            return saved;
        });

        AlbumDTO result = albumService.create(request);

        assertThat(result.title()).isEqualTo("Master of Puppets");
        assertThat(result.genreIds()).containsExactlyInAnyOrder(11L, 12L);
        assertThat(result.tracks()).extracting(TrackDTO::title).containsExactly("Battery", "Orion");
        verify(albumSearchIndex).clear();
    }

    @Test
    void createShouldThrowWhenArtistIsMissing() {
        AlbumDTO request = new AlbumDTO(null, "Reload", 1997, 99L, Set.of(), List.of());
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Artist not found: 99");
    }

    @Test
    void updateShouldReplaceGenresAndTracksAndClearSearchIndex() {
        Album album = album(41L, "Old Title", 1983, 3L);

        Genre oldGenre = genre(1L, "Old");
        oldGenre.getAlbums().add(album);
        album.getGenres().add(oldGenre);

        Track oldTrack = track(100L, "Old Track", 180, album);
        album.getTracks().add(oldTrack);

        Artist newArtist = artist(4L, "Megadeth");
        Genre newGenre = genre(7L, "Thrash");

        AlbumDTO request = new AlbumDTO(
                null,
                "Kill 'Em All",
                1983,
                4L,
                Set.of(7L),
                List.of(new TrackDTO(null, "Seek & Destroy", 415, 41L))
        );

        when(albumRepository.findWithAllById(41L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(4L)).thenReturn(Optional.of(newArtist));
        when(genreRepository.findById(7L)).thenReturn(Optional.of(newGenre));
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDTO result = albumService.update(41L, request);

        assertThat(result.title()).isEqualTo("Kill 'Em All");
        assertThat(result.artistId()).isEqualTo(4L);
        assertThat(result.genreIds()).containsExactly(7L);
        assertThat(result.tracks()).extracting(TrackDTO::title).containsExactly("Seek & Destroy");
        assertThat(oldGenre.getAlbums()).doesNotContain(album);
        verify(albumSearchIndex).clear();
    }

    @Test
    void updateShouldThrowWhenArtistIsMissing() {
        Album album = album(41L, "Old Title", 1983, 3L);
        AlbumDTO request = new AlbumDTO(null, "New Title", 1983, 99L, Set.of(), List.of());

        when(albumRepository.findWithAllById(41L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(41L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Artist not found: 99");
    }

    @Test
    void updateShouldThrowWhenGenreIsMissing() {
        Album album = album(41L, "Old Title", 1983, 3L);
        AlbumDTO request = new AlbumDTO(null, "New Title", 1983, 3L, Set.of(77L), List.of());

        when(albumRepository.findWithAllById(41L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(3L)).thenReturn(Optional.of(album.getArtist()));
        when(genreRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(41L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre not found: 77");
    }

    @Test
    void deleteShouldClearGenresDeleteAlbumAndClearSearchIndex() {
        Album album = album(50L, "Garage Days", 1987, 3L);
        Genre genre = genre(5L, "Metal");
        album.getGenres().add(genre);
        genre.getAlbums().add(album);

        when(albumRepository.findWithAllById(50L)).thenReturn(Optional.of(album));

        albumService.delete(50L);

        assertThat(album.getGenres()).isEmpty();
        assertThat(genre.getAlbums()).doesNotContain(album);
        verify(albumRepository).delete(album);
        verify(albumSearchIndex).clear();
    }

    @Test
    void searchWithJpqlShouldRejectInvalidYearRange() {
        assertThatThrownBy(() -> albumService.searchWithJpql(null, null, null, 2005, 1999, PageRequest.of(0, 10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("yearFrom must be less than or equal to yearTo");
    }

    @Test
    void searchWithJpqlShouldReturnCachedPageWithoutHittingRepository() {
        PageImpl<AlbumDTO> cachedPage = new PageImpl<>(List.of(
                new AlbumDTO(1L, "Ride the Lightning", 1984, 1L, Set.of(), List.of())
        ));

        when(albumSearchIndex.get(any(AlbumSearchCacheKey.class))).thenReturn(cachedPage);

        assertThat(albumService.searchWithJpql("Ride", null, null, null, null, PageRequest.of(0, 20)))
                .isSameAs(cachedPage);
        verify(albumRepository, never()).searchIdsByFiltersJpql(any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchWithJpqlShouldReturnEmptyPageAndCacheIt() {
        PageRequest pageable = PageRequest.of(1, 5);
        when(albumSearchIndex.get(any(AlbumSearchCacheKey.class))).thenReturn(null);
        when(albumRepository.searchIdsByFiltersJpql("%", "%metallica%", "%", 1980, 1990, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<AlbumDTO> result = albumService.searchWithJpql(null, " Metallica ", null, 1980, 1990, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(albumRepository, never()).findAllByIdIn(any());
        verify(albumSearchIndex).put(any(AlbumSearchCacheKey.class), any());
    }

    @Test
    void searchWithNativeShouldNormalizeFiltersUseDefaultPageAndPreserveIdOrder() {
        Album albumFour = album(4L, "Master of Puppets", 1986, 3L);
        Album albumNine = album(9L, "...And Justice for All", 1988, 3L);
        PageRequest normalizedPage = PageRequest.of(0, 20);

        when(albumSearchIndex.get(any(AlbumSearchCacheKey.class))).thenReturn(null);
        when(albumRepository.searchIdsByFiltersNative("%justice%", "%metallica%", "%thrash%", null, null, normalizedPage))
                .thenReturn(new PageImpl<>(List.of(9L, 4L), normalizedPage, 2));
        when(albumRepository.findAllByIdIn(List.of(9L, 4L))).thenReturn(List.of(albumFour, albumNine));

        List<AlbumDTO> result = albumService
                .searchWithNative(" Justice ", " Metallica ", " Thrash ", null, null, Pageable.unpaged())
                .getContent();

        assertThat(result).extracting(AlbumDTO::id).containsExactly(9L, 4L);
        verify(albumSearchIndex).put(any(AlbumSearchCacheKey.class), any());
    }

    @Test
    void searchWithJpqlShouldUseDefaultPageWhenPageableIsNull() {
        PageRequest normalizedPage = PageRequest.of(0, 20);

        when(albumSearchIndex.get(any(AlbumSearchCacheKey.class))).thenReturn(null);
        when(albumRepository.searchIdsByFiltersJpql("%title%", "%", "%", null, null, normalizedPage))
                .thenReturn(new PageImpl<>(List.of(), normalizedPage, 0));

        assertThat(albumService.searchWithJpql("title", null, null, null, null, null).getSize()).isEqualTo(20);
    }

    @Test
    void getByIdShouldThrowWhenAlbumIsMissing() {
        when(albumRepository.findWithAllById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.getById(15L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Album not found: 15");
    }

    private Artist artist(Long id, String name) {
        Artist artist = new Artist(name);
        ReflectionTestUtils.setField(artist, "id", id);
        return artist;
    }

    private Genre genre(Long id, String name) {
        Genre genre = new Genre(name);
        ReflectionTestUtils.setField(genre, "id", id);
        return genre;
    }

    private Album album(Long id, String title, Integer year, Long artistId) {
        Album album = new Album();
        ReflectionTestUtils.setField(album, "id", id);
        album.setTitle(title);
        album.setYear(year);
        album.setArtist(artist(artistId, "Artist " + artistId));
        album.setTracks(new ArrayList<>());
        album.setGenres(new HashSet<>());
        return album;
    }

    private Track track(Long id, String title, Integer durationSec, Album album) {
        Track track = new Track(title, durationSec);
        ReflectionTestUtils.setField(track, "id", id);
        track.setAlbum(album);
        return track;
    }
}
