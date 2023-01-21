package ro.sopa.statistifier.service.model;

public class ArtistTimelinePoint {

    private String time;
    private long listens;

    public ArtistTimelinePoint(String time, long listens) {
        this.time = time;
        this.listens = listens;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getListens() {
        return listens;
    }

    public void setListens(long listens) {
        this.listens = listens;
    }
}
