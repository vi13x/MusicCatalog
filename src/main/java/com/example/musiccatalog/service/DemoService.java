package com.example.musiccatalog.service;

import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.entity.Artist;
import com.example.musiccatalog.entity.Track;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.repository.AlbumRepository;
import com.example.musiccatalog.repository.ArtistRepository;
import com.example.musiccatalog.repository.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    private static final int DEMO_ALBUM_YEAR = 2024;
    private static final int DEMO_TRACK_DURATION_OK = 180;
    private static final int DEMO_TRACK_DURATION_BAD = 200;
    private static final String DEMO_TRACK_TITLE_OK = "Good track";
    private static final String DEMO_ALBUM_NO_TX = "NoTx Album";
    private static final String DEMO_ALBUM_TX = "Tx Album";

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
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.ALBUM_NOT_FOUND + id));
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

    public void createAlbumWithTracksNoTx(String artistName) {
        createAlbumWithTracksInternal(artistName, DEMO_ALBUM_NO_TX);
    }

    @Transactional
    public void createAlbumWithTracksTx(String artistName) {
        createAlbumWithTracksInternal(artistName, DEMO_ALBUM_TX);
    }

    private void createAlbumWithTracksInternal(String artistName, String albumTitle) {
        Artist artist = artistRepository.save(new Artist(artistName));
        Album album = albumRepository.save(new Album(albumTitle, DEMO_ALBUM_YEAR, artist));

        Track ok = new Track(DEMO_TRACK_TITLE_OK, DEMO_TRACK_DURATION_OK);
        ok.setAlbum(album);
        trackRepository.save(ok);

        Track bad = new Track(null, DEMO_TRACK_DURATION_BAD);
        bad.setAlbum(album);
        trackRepository.saveAndFlush(bad);
    }
}