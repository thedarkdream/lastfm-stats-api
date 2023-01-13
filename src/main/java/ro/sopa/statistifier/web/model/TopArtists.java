package ro.sopa.statistifier.web.model;

import java.util.List;

public class TopArtists {

    private List<ArtistListensObj> artists;

    public List<ArtistListensObj> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistListensObj> artists) {
        this.artists = artists;
    }
}
