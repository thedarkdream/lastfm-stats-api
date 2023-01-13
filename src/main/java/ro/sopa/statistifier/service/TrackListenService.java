package ro.sopa.statistifier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.sopa.statistifier.db.repository.TrackListenRepository;
import ro.sopa.statistifier.service.dto.ArtistListens;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrackListenService {

    @Autowired
    private TrackListenRepository trackListenRepository;

    public List<ArtistListens> findTopArtists(String username, int number) {
        List<Object[]> artistsAndCounts = trackListenRepository.findTopArtists(username, number);

        return artistsAndCounts.stream().map(this::toListens).collect(Collectors.toList());

    }

    private ArtistListens toListens(Object[] objects) {
        return new ArtistListens((String) objects[0], ((Long) objects[1]).intValue());
    }

}
