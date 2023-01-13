package ro.sopa.statistifier.service.dto;

public class ArtistListens {
    private String artist;
    private Integer count;

    public ArtistListens(String artist, Integer count) {
        this.artist = artist;
        this.count = count;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
