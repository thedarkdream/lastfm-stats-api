package ro.sopa.statistifier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.sopa.statistifier.db.repository.TrackListenRepository;
import ro.sopa.statistifier.service.dto.ArtistListens;
import ro.sopa.statistifier.service.model.ArtistTimelineEntry;
import ro.sopa.statistifier.service.model.ArtistTimelinePoint;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrackListenService {

    @Autowired
    private TrackListenRepository trackListenRepository;

    @Value("${dbType}")
    private String dbType;

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    public List<ArtistListens> findTopArtists(String username, int number) {
        List<Object[]> artistsAndCounts = trackListenRepository.findTopArtists(username, number, dbType);

        return artistsAndCounts.stream().map(this::toListens).collect(Collectors.toList());
    }

    public List<ArtistTimelineEntry> findArtistsTimeline(String username, ZonedDateTime from, ZonedDateTime to, int nrSteps) {

        if(to == null) {
            to = ZonedDateTime.now();
        }

        if(from == null) {
            from = trackListenRepository.findOldestListenDate(username);
        }

        // așe pe scurt
        List<String> artists = trackListenRepository.findTopArtists(username, 20, dbType).stream().map(o -> (String) o[0]).collect(Collectors.toList());

        ZoneId zoneId = from.getZone();

        long fromMilis = from.toInstant().getEpochSecond();
        long toMilis = to.toInstant().getEpochSecond();

        long interval = (toMilis - fromMilis) / nrSteps;

        List<ArtistTimelineEntry> entries = new ArrayList<>();

        for (String artist : artists) {

            ArtistTimelineEntry entry = new ArtistTimelineEntry(artist);
            entries.add(entry);

            List<ZonedDateTime> dates = trackListenRepository.findListenDates(username, artist, from, to);

            // divide the times based on nr of steps
            for (long currentMilis = fromMilis; currentMilis < toMilis; currentMilis += interval) {
                ZonedDateTime min = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentMilis), zoneId);
                ZonedDateTime max = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentMilis + interval), zoneId);
                String stepDate = min.format(formatter);
                long count = dates.stream().filter(d -> d.isAfter(min) && d.isBefore(max)).count();
                entry.add(new ArtistTimelinePoint(stepDate, count));
            }
        }

        return entries;

    }

    private ArtistListens toListens(Object[] objects) {
        return new ArtistListens((String) objects[0], ((Number) objects[1]).intValue());
    }

}
