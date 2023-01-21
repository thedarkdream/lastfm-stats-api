package ro.sopa.statistifier.service.model;

import java.util.ArrayList;
import java.util.List;

public class ArtistTimelineEntry {

    private String artist;

    private List<ArtistTimelinePoint> points = new ArrayList<>();

    public ArtistTimelineEntry(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<ArtistTimelinePoint> getPoints() {
        return points;
    }

    public void add(ArtistTimelinePoint point) {
        points.add(point);
    }
}
