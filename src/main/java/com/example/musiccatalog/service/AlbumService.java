package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Genre;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.AlbumMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;
import com.example.musiccatalog.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        GenreRepository genreRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
    }

    public List<AlbumDTO> getAll() {
        return albumRepository.findAll().stream().map(AlbumMapper::toDto).toList();
    }

    public AlbumDTO getById(Long id) {
        Album a = albumRepository.findWithAllById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));
        return AlbumMapper.toDto(a);
    }

    public AlbumDTO create(AlbumDTO dto) {
        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ARTIST_NOT_FOUND + dto.artistId()));

        Album album = new Album(dto.title(), dto.year(), artist);
        applyGenres(album, dto.genreIds());
        applyTracks(album, dto.tracks());

        return AlbumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDTO update(Long id, AlbumDTO dto) {
        Album album = albumRepository.findWithAllById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));

        album.setTitle(dto.title());
        album.setYear(dto.year());
        album.clearGenres();
        applyGenres(album, dto.genreIds());
        album.getTracks().clear();
        applyTracks(album, dto.tracks());

        return AlbumMapper.toDto(albumRepository.save(album));
    }

    private void applyGenres(Album album, Set<Long> genreIds) {
        Set<Long> ids = genreIds == null ? Set.of() : genreIds;
        for (Long gid : ids) {
            Genre g = genreRepository.findById(gid)
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.GENRE_NOT_FOUND + gid));
            album.addGenre(g);
        }
    }

    private void applyTracks(Album album, List<TrackDTO> trackDtos) {
        List<TrackDTO> list = trackDtos == null ? List.of() : trackDtos;
        for (TrackDTO t : list) {
            Track track = new Track(t.title(), t.durationSec());
            album.addTrack(track);
        }
    }

    public void delete(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));
        albumRepository.delete(album);
    }
}