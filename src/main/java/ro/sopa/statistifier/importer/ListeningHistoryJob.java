package ro.sopa.statistifier.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ro.sopa.statistifier.UserImportException;
import ro.sopa.statistifier.api.client.LastFMClient;
import ro.sopa.statistifier.api.model.ListeningHistory;
import ro.sopa.statistifier.api.model.Track;
import ro.sopa.statistifier.db.model.TrackListen;
import ro.sopa.statistifier.db.repository.TrackListenRepository;
import ro.sopa.statistifier.job.JobDescriptor;
import ro.sopa.statistifier.job.JobRegistry;
import ro.sopa.statistifier.job.JobType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ListeningHistoryJob {

    private static final Logger logger = LoggerFactory.getLogger(ListeningHistoryJob.class);

    @Autowired
    LastFMClient lastFMClient;

    @Value("${lastfm.api.key}")
    private String apiKey;

    @Autowired
    TrackListenRepository trackListenRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    JobRegistry jobRegistry;

    ObjectMapper objectMapper = new ObjectMapper();

    public void downloadAndPersistHistory(String username, Integer startPage, JobDescriptor jobDescriptor) {

        ZonedDateTime dateOfLastListen = trackListenRepository.findLatestListenDate(username);

        boolean shouldContinue = true;

        jobRegistry.setStep(jobDescriptor, startPage);

        logger.info("Fetching page " + startPage + "...");
        ListeningHistory page = lastFMClient.callRecentTracks(apiKey, username, startPage);
        shouldContinue = persist(page, username, dateOfLastListen);

        String nrPagesStr = page.getRecenttracks().getAttr().getTotalPages();
        Integer nrPages = Integer.parseInt(nrPagesStr);

        jobRegistry.setTotalStep(jobDescriptor, nrPages);

        // crashed @ 414
        //  Data truncation: Incorrect datetime value: '2015-03-29T03:56:00' for column 'date' at row 1
        for(int i = startPage + 1; i <= nrPages; i++) {
            if(!shouldContinue) {
                logger.info("Stopped because scrobbles are up to date.");
                break;
            }

            jobRegistry.setStep(jobDescriptor, i);

            logger.info("Fetching page " + i + " of " + nrPages + "...");
            page = lastFMClient.callRecentTracks(apiKey, username, i);
            shouldContinue = persist(page, username, dateOfLastListen);
            entityManager.clear();
        }

        logger.info("Finished!");
    }

    private boolean persist(ListeningHistory page, String username, ZonedDateTime dateOfLastListen) {
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
                    throw new UserImportException("Error while persisting listen history", e);
                }
            }
        }

        return true;
    }

    private boolean persist(Track track, String username, ZonedDateTime dateOfLastListen) {

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

    private static ZonedDateTime mapDate(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").withZone(ZoneId.of("UTC")).withLocale(Locale.US));
    }

}
