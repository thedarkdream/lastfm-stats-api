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
import ro.sopa.lastfm.db.model.Artist
import ro.sopa.lastfm.db.model.ArtistAlias
import ro.sopa.lastfm.db.model.TrackListen
import ro.sopa.lastfm.db.model.User
import ro.sopa.lastfm.db.repository.ArtistAliasRepository
import ro.sopa.lastfm.db.repository.ArtistRepository
import ro.sopa.lastfm.db.repository.TrackListenRepository
import ro.sopa.lastfm.db.repository.UserRepository
import ro.sopa.lastfm.exception.UserImportException
import ro.sopa.lastfm.job.JobDescriptor
import ro.sopa.lastfm.job.JobRegistry
import ro.sopa.lastfm.service.ArtistService
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class ListeningHistoryJob(val lastFMClient: LastFMClient,
                          val trackListenRepository: TrackListenRepository,
                          val artistRepository: ArtistRepository,
                          val artistAliasRepository: ArtistAliasRepository,
                          val entityManager: EntityManager,
                          val artistService: ArtistService,
                          val userRepository: UserRepository,
                          val jobRegistry: JobRegistry
) {

    private val logger = LoggerFactory.getLogger(ListeningHistoryJob::class.java)

    @Value("\${lastfm.api.key}")
    private lateinit var apiKey: String

    private val gson = Gson();

    fun downloadAndPersistHistory(username: String, startPage: Int, jobDescriptor: JobDescriptor) {

        var maybeUser = userRepository.findByUsername(username)
        val user = maybeUser ?: createUser(username)

        val dateOfLastListen: ZonedDateTime? = trackListenRepository.findLatestListenDate(user.id!!)
        var shouldContinue = true

        val userId = getOrCreateUser(username)

        jobRegistry.setStep(jobDescriptor, startPage)

        logger.info("Fetching page $startPage...")

        var page: ListeningHistory = lastFMClient.callRecentTracks(apiKey, username, startPage)
        shouldContinue = persist(page, userId, dateOfLastListen)
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
            shouldContinue = persist(page, userId, dateOfLastListen)
            entityManager.clear()
        }
        logger.info("Finished!")
    }

    private fun getOrCreateUser(username: String): Int {
        val user = userRepository.findByUsername(username);
        return user?.id ?: createUser(username).id!!;
    }

    private fun createUser(username: String): User {
        val user = User(null, username)
        return userRepository.save(user)
    }


    private fun persist(page: ListeningHistory, userId: Int, dateOfLastListen: ZonedDateTime?): Boolean {
        for (i in 0..page.recenttracks?.track?.size!! - 1) {
            val t: Track = page.recenttracks?.track?.get(i) ?: throw RuntimeException("No tracks returned");
            try {
                val shouldContinue = persist(t, userId, dateOfLastListen)
                if (!shouldContinue) {
                    return false
                }
            } catch (thr: Throwable) {
                logger.error(
                    "Error while persisting track on page " + page.recenttracks?.attr?.page + " with index = " + i, thr
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

    private fun persist(track: Track, userId: Int, dateOfLastListen: ZonedDateTime?): Boolean {
        val listen: TrackListen = mapTrack(track, userId)
        if (listen.date == dateOfLastListen) {
            return false
        }
        trackListenRepository.save(listen)
        return true
    }

    private fun mapTrack(track: Track, userId: Int): TrackListen {

        val artistId = findArtist(track);

        val listen = TrackListen(track = track.name ?: "Unknown", userId = userId, artistId = artistId)
        listen.album = track.album?.text
        listen.albumId = track.album?.mbid
        listen.date = mapDate(track.date?.text!!)

        return listen
    }

    private fun findArtist(track: Track): Int {
        if(track.artist != null) {
            var artistId = artistAliasRepository.findArtistIdByAlias(track.artist?.text!!)

            return artistId ?: handleMissingArtistAlias(track);
        }

        throw RuntimeException("Invalid artist received")
    }

    private fun handleMissingArtistAlias(track: Track): Int {
        val corrections = lastFMClient.callCorrections(apiKey, track.artist?.text!!)

        val correctName = corrections.corrections?.correction?.artist?.name!!

        var artist = artistRepository.findByName(correctName) ?: handleMissingArtist(corrections.corrections?.correction?.artist!!)

        return createArtistAlias(artist, track.artist?.text!!);
    }

    private fun handleMissingArtist(artist: ro.sopa.lastfm.api.model.correction.Artist): Artist {
        val artistDb = Artist(null, artist.name!!, artist.mbid)
        val savedArtist = artistRepository.save(artistDb)

        artistService.downloadTags(savedArtist);

        return savedArtist;
    }

    private fun createArtistAlias(artist: Artist, aliasName: String): Int {
        val alias = ArtistAlias(null, artist.id!!, aliasName)
        val persistedAlias = artistAliasRepository.save(alias)
        return persistedAlias.id!!.toInt()
    }

    private fun mapDate(date: String): ZonedDateTime? = ZonedDateTime.parse(date,
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").withZone(ZoneId.of("UTC")).withLocale(Locale.US))
}