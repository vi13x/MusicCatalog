package com.example.musiccatalog.service;

import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class AlbumSearchCacheKey {

    private final AlbumSearchMode mode;
    private final String title;
    private final String artistName;
    private final String genreName;
    private final Integer yearFrom;
    private final Integer yearTo;
    private final int pageNumber;
    private final int pageSize;

    public AlbumSearchCacheKey(AlbumSearchMode mode, AlbumSearchCriteria criteria, Pageable pageable) {
        this.mode = mode;
        this.title = criteria.title();
        this.artistName = criteria.artistName();
        this.genreName = criteria.genreName();
        this.yearFrom = criteria.yearFrom();
        this.yearTo = criteria.yearTo();
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AlbumSearchCacheKey that)) {
            return false;
        }
        return pageNumber == that.pageNumber
                && pageSize == that.pageSize
                && mode == that.mode
                && Objects.equals(title, that.title)
                && Objects.equals(artistName, that.artistName)
                && Objects.equals(genreName, that.genreName)
                && Objects.equals(yearFrom, that.yearFrom)
                && Objects.equals(yearTo, that.yearTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, title, artistName, genreName, yearFrom, yearTo, pageNumber, pageSize);
    }
}