package ro.sopa.statistifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.sopa.statistifier.service.TrackListenService;
import ro.sopa.statistifier.service.dto.ArtistListens;
import ro.sopa.statistifier.web.model.ArtistListensObj;
import ro.sopa.statistifier.web.model.TopArtists;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private TrackListenService trackListenService;

    @GetMapping("/{username}/artists")
    public TopArtists artists(@PathVariable String username) {
        List<ArtistListens> artistListens = trackListenService.findTopArtists(username, 30);

        TopArtists ta = new TopArtists();
        ta.setArtists(artistListens.stream().map(this::toArtist).collect(Collectors.toList()));
        return ta;
    }

    private ArtistListensObj toArtist(ArtistListens artistListens) {
        ArtistListensObj o = new ArtistListensObj();
        o.setListens(artistListens.getCount());
        o.setName(artistListens.getArtist());
        return o;
    }

}
