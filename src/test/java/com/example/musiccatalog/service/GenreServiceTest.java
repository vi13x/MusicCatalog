package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.musiccatalog.dto.GenreDTO;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.GenreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AlbumSearchIndex albumSearchIndex;

    @InjectMocks
    private GenreService genreService;

    @Test
    void getAllShouldReturnMappedGenres() {
        when(genreRepository.findAll()).thenReturn(List.of(new Genre("Metal"), new Genre("Jazz")));

        assertThat(genreService.getAll()).extracting(GenreDTO::name).containsExactly("Metal", "Jazz");
    }

    @Test
    void getByIdShouldReturnGenre() {
        when(genreRepository.findById(2L)).thenReturn(Optional.of(new Genre("Metal")));

        assertThat(genreService.getById(2L).name()).isEqualTo("Metal");
    }

    @Test
    void createShouldSaveGenreAndClearSearchIndex() {
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GenreDTO result = genreService.create(new GenreDTO(null, "Metal"));

        assertThat(result.name()).isEqualTo("Metal");
        verify(albumSearchIndex).clear();
    }

    @Test
    void updateShouldSaveGenreAndClearSearchIndex() {
        Genre genre = new Genre("Metal");
        when(genreRepository.findById(5L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GenreDTO result = genreService.update(5L, new GenreDTO(null, "Heavy Metal"));

        assertThat(result.name()).isEqualTo("Heavy Metal");
        verify(albumSearchIndex).clear();
    }

    @Test
    void deleteShouldDeleteGenreAndClearSearchIndex() {
        Genre genre = new Genre("Metal");
        when(genreRepository.findById(7L)).thenReturn(Optional.of(genre));

        genreService.delete(7L);

        verify(genreRepository).delete(genre);
        verify(albumSearchIndex).clear();
    }

    @Test
    void getByIdShouldThrowWhenGenreIsMissing() {
        when(genreRepository.findById(17L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getById(17L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre not found: 17");
    }
}
