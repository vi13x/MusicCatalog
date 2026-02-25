package com.example.musiccatalog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musiccatalog.dto.AlbumDTO;
import com.example.musiccatalog.dto.TrackDTO;
import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.entity.constant.EntityType;
import com.example.musiccatalog.exception.EntityNotFoundException;
import com.example.musiccatalog.mapper.AlbumMapper;
import com.example.musiccatalog.mapper.TrackMapper;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumMapper albumMapper;
    private final TrackMapper trackMapper;

    @Transactional
    public AlbumDTO create(AlbumDTO dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new IllegalArgumentException("Album title is blank");
        }
        Long artistId = dto.artist() != null ? dto.artist().id() : null;
        if (artistId == null) {
            throw new IllegalArgumentException("Artist id is required");
        }

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ARTIST, "id", artistId));

        Album album = albumMapper.toEntity(dto);
        album.setArtist(artist);
        Album savedAlbum = albumRepository.save(album);

        if (dto.tracks() != null && !dto.tracks().isEmpty()) {
            List<Track> tracks = new ArrayList<>();
            for (TrackDTO trackDto : dto.tracks()) {
                Track track = trackMapper.toEntity(trackDto);
                track.setAlbum(savedAlbum);
                tracks.add(track);
            }
            savedAlbum.getTracks().addAll(tracks);
            savedAlbum = albumRepository.save(savedAlbum);
        }

        return albumMapper.toDTO(savedAlbum);
    }

    @Transactional
    public AlbumDTO findById(long id) {
        Album entity = albumRepository.findWithArtistAndTracksById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ALBUM, "id", id));
        return albumMapper.toDTO(entity);
    }

    public List<AlbumDTO> findAll() {
        return albumRepository.findAll().stream()
                .map(albumMapper::toDTO)
                .toList();
    }

    @Transactional
    public AlbumDTO update(AlbumDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Album id is required for update");
        }
        if (dto.title() == null || dto.title().isBlank()) {
            throw new IllegalArgumentException("Album title is blank");
        }

        Album album = albumRepository.findWithArtistAndTracksById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.ALBUM, "id", dto.id()));

        album.setTitle(dto.title().trim());
        album.setReleaseYear(dto.releaseYear());

        if (dto.artist() != null && dto.artist().id() != null) {
            Artist artist = artistRepository.findById(dto.artist().id())
                    .orElseThrow(() -> new EntityNotFoundException(EntityType.ARTIST, "id", dto.artist().id()));
            album.setArtist(artist);
        }

        album.getTracks().clear();
        if (dto.tracks() != null && !dto.tracks().isEmpty()) {
            for (TrackDTO trackDto : dto.tracks()) {
                Track track = trackMapper.toEntity(trackDto);
                track.setAlbum(album);
                album.getTracks().add(track);
            }
        }

        album = albumRepository.save(album);
        return albumMapper.toDTO(album);
    }

    public void removeById(long id) {
        albumRepository.deleteById(id);
    }
}
