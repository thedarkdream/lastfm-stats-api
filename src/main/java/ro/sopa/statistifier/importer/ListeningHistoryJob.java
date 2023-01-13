package ro.sopa.statistifier.importer;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.sopa.statistifier.api.client.LastFMClient;
import ro.sopa.statistifier.api.model.ListeningHistory;
import ro.sopa.statistifier.api.model.Track;
import ro.sopa.statistifier.db.model.TrackListen;
import ro.sopa.statistifier.db.repository.TrackListenRepository;

@Component
public class ListeningHistoryJob {

    private static final Logger logger = LoggerFactory.getLogger(ListeningHistoryJob.class);

    @Autowired
    LastFMClient lastFMClient;

    @Autowired
    TrackListenRepository trackListenRepository;

    public void downloadAndPersistHistory(String username) {

        String apiKey = "a815b9a9b884cbd5960bc8568b11f2a";

        logger.debug("Fetching the first page...");
        ListeningHistory page = lastFMClient.callRecentTracks(apiKey, username, 0);
        persist(page, username);

        String nrPagesStr = page.getRecenttracks().getAttr().getTotalPages();
        Integer nrPages = Integer.parseInt(nrPagesStr);

        for(int i = 0; i <= nrPages; i++) {
            logger.debug("Fetching page " + i + " of " + nrPages + "...");
            page = lastFMClient.callRecentTracks(apiKey, username, i);
            persist(page, username);
        }

    }

    private void persist(ListeningHistory page, String username) {
        page.getRecenttracks().getTrack().forEach(t -> persist(t, username));
    }

    private void persist(Track track, String username) {

        TrackListen listen = mapTrack(track, username);
        trackListenRepository.save(listen);

    }

    private TrackListen mapTrack(Track track, String username) {
        TrackListen listen = new TrackListen();

        listen.setUsername(username);
        listen.setAlbum(track.getAlbum().getText());
        listen.setAlbumId(track.getAlbum().getMbid());
        listen.setArtist(track.getArtist().getText());
        listen.setArtist(track.getArtist().getMbid());
        listen.setTrack(track.getName());
        listen.setDate(track.getDate().getText());

        return listen;
    }

}
