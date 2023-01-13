package ro.sopa.statistifier.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.sopa.statistifier.api.client.LastFMClient;
import ro.sopa.statistifier.api.model.ListeningHistory;
import ro.sopa.statistifier.api.model.Track;
import ro.sopa.statistifier.db.model.TrackListen;
import ro.sopa.statistifier.db.repository.TrackListenRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ListeningHistoryJob {

    private static final Logger logger = LoggerFactory.getLogger(ListeningHistoryJob.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    LastFMClient lastFMClient;

    @Autowired
    TrackListenRepository trackListenRepository;

    @Autowired
    EntityManager entityManager;

    ObjectMapper objectMapper = new ObjectMapper();

    public void downloadAndPersistHistory(String username, Integer startPage) {

        LocalDateTime dateOfLastListen = trackListenRepository.findLatestListenDate(username);

        boolean shouldContinue = true;

        String apiKey = "ca959cbe2c1f179ccce738c66e3d12df";

        logger.info("Fetching the first page...");
        ListeningHistory page = lastFMClient.callRecentTracks(apiKey, username, startPage);
        shouldContinue = persist(page, username, dateOfLastListen);

        String nrPagesStr = page.getRecenttracks().getAttr().getTotalPages();
        Integer nrPages = Integer.parseInt(nrPagesStr);

        // crashed @ 414
        //  Data truncation: Incorrect datetime value: '2015-03-29T03:56:00' for column 'date' at row 1
        for(int i = startPage + 1; i <= nrPages; i++) {
            if(!shouldContinue) {
                logger.info("Stopped because scrobbles are up to date.");
                break;
            }
            logger.info("Fetching page " + i + " of " + nrPages + "...");
            page = lastFMClient.callRecentTracks(apiKey, username, i);
            shouldContinue = persist(page, username, dateOfLastListen);
            entityManager.clear();
        }

        logger.info("Finished!");
    }

    private boolean persist(ListeningHistory page, String username, LocalDateTime dateOfLastListen) {
        for(int i = 0; i < page.getRecenttracks().getTrack().size(); i++) {
            Track t = page.getRecenttracks().getTrack().get(i);

            try {
                boolean shouldContinue = persist(t, username, dateOfLastListen);
                if(!shouldContinue) {
                    return false;
                }
            } catch(Throwable thr) {
                logger.error("Error while persisting track on page " + page.getRecenttracks().getAttr().getPage() + " with index = " + i);
                try {
                    logger.error(objectMapper.writeValueAsString(t));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }

    private boolean persist(Track track, String username, LocalDateTime dateOfLastListen) {

        TrackListen listen = mapTrack(track, username);

        if(listen.getDate().equals(dateOfLastListen)) {
            return false;
        }

        trackListenRepository.save(listen);

        return true;
    }

    private TrackListen mapTrack(Track track, String username) {
        TrackListen listen = new TrackListen();

        listen.setUsername(username);
        listen.setAlbum(track.getAlbum().getText());
        listen.setAlbumId(track.getAlbum().getMbid());
        listen.setArtist(track.getArtist().getText());
        listen.setArtistId(track.getArtist().getMbid());
        listen.setTrack(track.getName());
        listen.setDate(mapDate(track.getDate().getText()));

        return listen;
    }

    private static LocalDateTime mapDate(String date) {
        return LocalDateTime.parse(date.replace("Sep", "Sept"), DateTimeFormatter.ofPattern("dd MMM yyyy, kk:mm"));
    }

}
