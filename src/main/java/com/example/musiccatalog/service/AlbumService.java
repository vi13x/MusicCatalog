package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.BadRequestException;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.AlbumMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;
import com.example.musiccatalog.repository.GenreRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final AlbumSearchIndex albumSearchIndex;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        GenreRepository genreRepository,
                        AlbumSearchIndex albumSearchIndex) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.albumSearchIndex = albumSearchIndex;
    }

    @Transactional(readOnly = true)
    public List<AlbumDTO> getAll() {
        return albumRepository.findAll().stream().map(AlbumMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AlbumDTO getById(Long id) {
        Album album = albumRepository.findWithAllById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));
        return AlbumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public Page<AlbumDTO> searchWithJpql(String title,
                                         String artistName,
                                         String genreName,
                                         Integer yearFrom,
                                         Integer yearTo,
                                         Pageable pageable) {
        return search(AlbumSearchMode.JPQL, title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AlbumDTO> searchWithNative(String title,
                                           String artistName,
                                           String genreName,
                                           Integer yearFrom,
                                           Integer yearTo,
                                           Pageable pageable) {
        return search(AlbumSearchMode.NATIVE, title, artistName, genreName, yearFrom, yearTo, pageable);
    }

    @Transactional
    public AlbumDTO create(AlbumDTO dto) {
        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ARTIST_NOT_FOUND + dto.artistId()));

        Album album = new Album(dto.title(), dto.year(), artist);
        applyGenres(album, dto.genreIds());
        applyTracks(album, dto.tracks());

        AlbumDTO saved = AlbumMapper.toDto(albumRepository.save(album));
        invalidateSearchIndex();
        return saved;
    }

    @Transactional
    public AlbumDTO update(Long id, AlbumDTO dto) {
        Album album = albumRepository.findWithAllById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));

        album.setTitle(dto.title());
        album.setYear(dto.year());
        album.clearGenres();
        applyGenres(album, dto.genreIds());
        album.getTracks().clear();
        applyTracks(album, dto.tracks());

        AlbumDTO saved = AlbumMapper.toDto(albumRepository.save(album));
        invalidateSearchIndex();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Album album = albumRepository.findWithAllById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));
        album.clearGenres();
        albumRepository.delete(album);
        invalidateSearchIndex();
    }

    private Page<AlbumDTO> search(AlbumSearchMode mode,
                                  String title,
                                  String artistName,
                                  String genreName,
                                  Integer yearFrom,
                                  Integer yearTo,
                                  Pageable pageable) {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria(title, artistName, genreName, yearFrom, yearTo)
                .normalize();
        validateYearRange(criteria.yearFrom(), criteria.yearTo());

        Pageable normalizedPageable = normalizePageable(pageable);
        AlbumSearchCacheKey cacheKey = new AlbumSearchCacheKey(mode, criteria, normalizedPageable);
        Page<AlbumDTO> cachedPage = albumSearchIndex.get(cacheKey);
        if (cachedPage != null) {
            return cachedPage;
        }

        String titlePattern = toContainsPattern(criteria.title());
        String artistNamePattern = toContainsPattern(criteria.artistName());
        String genreNamePattern = toContainsPattern(criteria.genreName());

        Page<Long> albumIdPage = switch (mode) {
            case JPQL -> albumRepository.searchIdsByFiltersJpql(
                    titlePattern,
                    artistNamePattern,
                    genreNamePattern,
                    criteria.yearFrom(),
                    criteria.yearTo(),
                    normalizedPageable
            );
            case NATIVE -> albumRepository.searchIdsByFiltersNative(
                    titlePattern,
                    artistNamePattern,
                    genreNamePattern,
                    criteria.yearFrom(),
                    criteria.yearTo(),
                    normalizedPageable
            );
        };

        Page<AlbumDTO> resultPage = mapAlbumPage(albumIdPage, normalizedPageable);
        albumSearchIndex.put(cacheKey, resultPage);
        return resultPage;
    }

    private Page<AlbumDTO> mapAlbumPage(Page<Long> albumIdPage, Pageable pageable) {
        List<Long> albumIds = albumIdPage.getContent();
        if (albumIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<Long, Album> albumsById = albumRepository.findAllByIdIn(albumIds).stream()
                .collect(Collectors.toMap(Album::getId, Function.identity(), (left, right) -> left));

        List<AlbumDTO> albums = albumIds.stream()
                .map(albumsById::get)
                .map(AlbumMapper::toDto)
                .toList();

        return new PageImpl<>(albums, pageable, albumIdPage.getTotalElements());
    }

    private Pageable normalizePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.of(0, 20);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    private void validateYearRange(Integer yearFrom, Integer yearTo) {
        if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
            throw new BadRequestException(ErrorMessages.INVALID_ALBUM_YEAR_RANGE);
        }
    }

    private String toContainsPattern(String value) {
        return value == null ? "%" : "%" + value + "%";
    }

    private void applyGenres(Album album, Set<Long> genreIds) {
        Set<Long> ids = genreIds == null ? Set.of() : genreIds;
        for (Long genreId : ids) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.GENRE_NOT_FOUND + genreId));
            album.addGenre(genre);
        }
    }

    private void applyTracks(Album album, List<TrackDTO> trackDtos) {
        List<TrackDTO> tracks = trackDtos == null ? List.of() : trackDtos;
        for (TrackDTO trackDto : tracks) {
            Track track = new Track(trackDto.title(), trackDto.durationSec());
            album.addTrack(track);
        }
    }

    private void invalidateSearchIndex() {
        albumSearchIndex.clear();
    }
}