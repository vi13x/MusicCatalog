package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.ArtistDTO;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.ArtistRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumSearchIndex albumSearchIndex;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void getAllShouldReturnMappedArtists() {
        when(artistRepository.findAll()).thenReturn(List.of(new Artist("Metallica"), new Artist("Megadeth")));

        assertThat(artistService.getAll()).extracting(ArtistDTO::name).containsExactly("Metallica", "Megadeth");
    }

    @Test
    void getByIdShouldReturnArtist() {
        when(artistRepository.findById(3L)).thenReturn(Optional.of(new Artist("Metallica")));

        assertThat(artistService.getById(3L).name()).isEqualTo("Metallica");
    }

    @Test
    void createShouldSaveArtistAndClearSearchIndex() {
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArtistDTO result = artistService.create(new ArtistDTO(null, "Metallica"));

        assertThat(result.name()).isEqualTo("Metallica");
        verify(albumSearchIndex).clear();
    }

    @Test
    void updateShouldSaveArtistAndClearSearchIndex() {
        Artist artist = new Artist("Metallica");
        when(artistRepository.findById(4L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(artist)).thenReturn(artist);

        ArtistDTO result = artistService.update(4L, new ArtistDTO(null, "Megadeth"));

        assertThat(result.name()).isEqualTo("Megadeth");
        verify(albumSearchIndex).clear();
    }

    @Test
    void deleteShouldLoadArtistWithAlbumsAndClearSearchIndex() {
        Artist artist = new Artist("Metallica");
        when(artistRepository.findByIdWithAlbums(4L)).thenReturn(Optional.of(artist));

        artistService.delete(4L);

        verify(artistRepository).delete(artist);
        verify(albumSearchIndex).clear();
    }

    @Test
    void deleteShouldThrowWhenArtistIsMissing() {
        when(artistRepository.findByIdWithAlbums(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Artist not found: 99");
    }

    @Test
    void getByIdShouldThrowWhenArtistIsMissing() {
        when(artistRepository.findById(13L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.getById(13L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Artist not found: 13");
    }
}
