package com.example.musiccatalog.service;

import java.util.Locale;

public record AlbumSearchCriteria(
        String title,
        String artistName,
        String genreName,
        Integer yearFrom,
        Integer yearTo
) {

    public AlbumSearchCriteria normalize() {
        return new AlbumSearchCriteria(
                normalizeValue(title),
                normalizeValue(artistName),
                normalizeValue(genreName),
                yearFrom,
                yearTo
        );
    }

    private static String normalizeValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed.toLowerCase(Locale.ROOT);
    }
}