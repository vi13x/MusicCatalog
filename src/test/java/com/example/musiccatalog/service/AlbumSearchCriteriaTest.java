package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AlbumSearchCriteriaTest {

    @Test
    void normalizeShouldTrimAndLowercaseValues() {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria(" Master ", " METALLICA ", " Heavy Metal ", 1984, 1986);

        AlbumSearchCriteria normalized = criteria.normalize();

        assertThat(normalized.title()).isEqualTo("master");
        assertThat(normalized.artistName()).isEqualTo("metallica");
        assertThat(normalized.genreName()).isEqualTo("heavy metal");
        assertThat(normalized.yearFrom()).isEqualTo(1984);
        assertThat(normalized.yearTo()).isEqualTo(1986);
    }

    @Test
    void normalizeShouldConvertNullAndBlankStringsToNull() {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria(null, "   ", "", null, null);

        AlbumSearchCriteria normalized = criteria.normalize();

        assertThat(normalized.title()).isNull();
        assertThat(normalized.artistName()).isNull();
        assertThat(normalized.genreName()).isNull();
    }
}
