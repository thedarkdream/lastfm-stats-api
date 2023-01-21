package ro.sopa.statistifier.web.model;

import java.util.ArrayList;
import java.util.List;

public class ArtistsTimeline {

    private List<ArtistTimeline> artistTimelines = new ArrayList<>();

    public List<ArtistTimeline> getArtistTimelines() {
        return artistTimelines;
    }

    public void setArtistTimelines(List<ArtistTimeline> artistTimelines) {
        this.artistTimelines = artistTimelines;
    }
}
