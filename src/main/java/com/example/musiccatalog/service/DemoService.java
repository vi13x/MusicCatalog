package com.example.musiccatalog.service;

import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;
import com.example.musiccatalog.repository.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;

    public DemoService(AlbumRepository albumRepository,
                       ArtistRepository artistRepository,
                       TrackRepository trackRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
    }

    public int nPlusOneDemo(List<Long> albumIds) {
        int total = 0;
        for (Long id : albumIds) {
            Album a = albumRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Album not found: " + id));
            total += a.getTracks().size();
        }
        return total;
    }

    public int nPlusOneFixed(List<Long> albumIds) {
        List<Album> albums = albumRepository.findAllByIdIn(albumIds);
        int total = 0;
        for (Album a : albums) {
            total += a.getTracks().size();
        }
        return total;
    }

    public void createAlbumWithTracks_NoTx(String artistName) {
        Artist artist = artistRepository.save(new Artist(artistName));
        Album album = albumRepository.save(new Album("NoTx Album", 2024, artist));

        Track ok = new Track("Good track", 180);
        ok.setAlbum(album);
        trackRepository.save(ok);

        Track bad = new Track(null, 200);
        bad.setAlbum(album);
        trackRepository.saveAndFlush(bad);
    }

    @Transactional
    public void createAlbumWithTracks_Tx(String artistName) {
        Artist artist = artistRepository.save(new Artist(artistName));
        Album album = albumRepository.save(new Album("Tx Album", 2024, artist));

        Track ok = new Track("Good track", 180);
        ok.setAlbum(album);
        trackRepository.save(ok);

        Track bad = new Track(null, 200);
        bad.setAlbum(album);
        trackRepository.saveAndFlush(bad);
    }
}