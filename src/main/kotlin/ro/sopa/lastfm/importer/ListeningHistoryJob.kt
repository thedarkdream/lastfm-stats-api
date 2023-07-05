package ro.sopa.lastfm.importer

import com.fasterxml.jackson.core.JsonProcessingException
import com.google.gson.Gson
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ro.sopa.lastfm.api.client.LastFMClient
import ro.sopa.lastfm.api.model.ListeningHistory
import ro.sopa.lastfm.api.model.Track
import ro.sopa.lastfm.db.model.TrackListen
import ro.sopa.lastfm.db.repository.TrackListenRepository
import ro.sopa.lastfm.exception.UserImportException
import ro.sopa.lastfm.job.JobDescriptor
import ro.sopa.lastfm.job.JobRegistry
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class ListeningHistoryJob(val lastFMClient: LastFMClient,
                          val trackListenRepository: TrackListenRepository,
                          val entityManager: EntityManager,
                          val jobRegistry: JobRegistry
) {

    private val logger = LoggerFactory.getLogger(ListeningHistoryJob::class.java)

    @Value("\${lastfm.api.key}")
    private val apiKey: String? = null

    private val gson = Gson();

    fun downloadAndPersistHistory(username: String, startPage: Int, jobDescriptor: JobDescriptor) {

        val dateOfLastListen: ZonedDateTime? = trackListenRepository.findLatestListenDate(username)
        var shouldContinue = true

        jobRegistry.setStep(jobDescriptor, startPage)

        logger.info("Fetching page $startPage...")

        var page: ListeningHistory = lastFMClient.callRecentTracks(apiKey, username, startPage)
        shouldContinue = persist(page, username, dateOfLastListen)
        val nrPagesStr: String? = page.recenttracks?.attr?.totalPages

        val nrPages = nrPagesStr?.toInt() ?: 1
        jobRegistry.setTotalStep(jobDescriptor, nrPages)

        for (i in startPage + 1..nrPages) {
            if (!shouldContinue) {
                logger.info("Stopped because scrobbles are up to date.")
                break
            }
            jobRegistry.setStep(jobDescriptor, i)
            logger.info("Fetching page $i of $nrPages...")
            page = lastFMClient.callRecentTracks(apiKey, username, i)
            shouldContinue = persist(page, username, dateOfLastListen)
            entityManager.clear()
        }
        logger.info("Finished!")
    }


    private fun persist(page: ListeningHistory, username: String, dateOfLastListen: ZonedDateTime?): Boolean {
        for (i in 0..page.recenttracks?.track?.size!! - 1) {
            val t: Track = page.recenttracks?.track?.get(i) ?: throw RuntimeException("No tracks returned");
            try {
                val shouldContinue = persist(t, username, dateOfLastListen)
                if (!shouldContinue) {
                    return false
                }
            } catch (thr: Throwable) {
                logger.error(
                    "Error while persisting track on page " + page.recenttracks?.attr?.page + " with index = " + i
                )
                try {
                    logger.error(gson.toJson(t))
                } catch (e: JsonProcessingException) {
                    throw UserImportException("Error while persisting listen history", e)
                }
            }
        }
        return true
    }

    private fun persist(track: Track, username: String, dateOfLastListen: ZonedDateTime?): Boolean {
        val listen: TrackListen = mapTrack(track, username)
        if (listen.date == dateOfLastListen) {
            return false
        }
        trackListenRepository.save(listen)
        return true
    }

    private fun mapTrack(track: Track, username: String): TrackListen {
        val listen = TrackListen(track = track.name ?: "Unknown", username = username, artist = track.artist?.text ?: "Unknown")
        listen.album = track.album?.text
        listen.albumId = track.album?.mbid
        listen.artistId = track.artist?.mbid
        listen.date = mapDate(track.date?.text!!)
        return listen
    }

    private fun mapDate(date: String): ZonedDateTime? = ZonedDateTime.parse(date,
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").withZone(ZoneId.of("UTC")).withLocale(Locale.US))
}