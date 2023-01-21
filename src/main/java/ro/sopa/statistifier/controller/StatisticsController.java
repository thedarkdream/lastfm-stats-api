package ro.sopa.statistifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.sopa.statistifier.service.TrackListenService;
import ro.sopa.statistifier.service.dto.ArtistListens;
import ro.sopa.statistifier.service.model.ArtistTimelineEntry;
import ro.sopa.statistifier.service.model.ArtistTimelinePoint;
import ro.sopa.statistifier.web.model.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private TrackListenService trackListenService;

    @GetMapping("/{username}/artists")
    public TopArtists artists(@PathVariable String username, @RequestParam(required = false) Integer number) {
        List<ArtistListens> artistListens = trackListenService.findTopArtists(username, number == null ? 20 : number);

        TopArtists ta = new TopArtists();
        ta.setArtists(artistListens.stream().map(this::toArtist).collect(Collectors.toList()));
        return ta;
    }

    @GetMapping("/{username}/artists/timeline")
    public ArtistsTimeline artistTimeline(@PathVariable String username,
                                          @RequestParam(required = false) String dateFrom,
                                          @RequestParam(required = false) String dateTo,
                                          @RequestParam(required = false) Integer nrSteps,
                                          @RequestParam(required = false) Integer nrArtists) {

        ArtistsTimeline artistsTimeline = new ArtistsTimeline();

        ZonedDateTime startDate = dateFrom != null ? ZonedDateTime.parse(dateFrom, DateTimeFormatter.ISO_DATE) : null;
        ZonedDateTime endDate = dateFrom != null ? ZonedDateTime.parse(dateTo, DateTimeFormatter.ISO_DATE) : null;

        List<ArtistTimelineEntry> entries = trackListenService.findArtistsTimeline(username, startDate, endDate, nrSteps != null ? nrSteps : 30, nrArtists != null ? nrArtists : 10);

        entries.stream().forEach(e -> artistsTimeline.getArtistTimelines().add(mapTimeline(e)));

        return artistsTimeline;
    }

    private ArtistTimeline mapTimeline(ArtistTimelineEntry e) {
        ArtistTimeline result = new ArtistTimeline();
        result.setArtist(e.getArtist());
        result.setPoints(mapPoints(e.getPoints()));
        return result;
    }

    private List<TimelinePoint> mapPoints(List<ArtistTimelinePoint> points) {
        return points.stream().map(p -> new TimelinePoint(p.getTime(), p.getListens())).collect(Collectors.toList());
    }

    private ArtistListensObj toArtist(ArtistListens artistListens) {
        ArtistListensObj o = new ArtistListensObj();
        o.setListens(artistListens.getCount());
        o.setName(artistListens.getArtist());
        return o;
    }

}
