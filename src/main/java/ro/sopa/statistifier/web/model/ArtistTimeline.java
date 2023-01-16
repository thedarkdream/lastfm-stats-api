package ro.sopa.statistifier.web.model;

import java.util.List;

public class ArtistTimeline {

    private String artist;

    private List<TimelinePoint> points;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<TimelinePoint> getPoints() {
        return points;
    }

    public void setPoints(List<TimelinePoint> points) {
        this.points = points;
    }
}
