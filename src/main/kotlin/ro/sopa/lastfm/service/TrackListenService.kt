package ro.sopa.lastfm.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.repository.TrackListenRepository
import ro.sopa.lastfm.db.repository.TrackListenRepositoryExtensions.findTopArtists
import ro.sopa.lastfm.service.dto.ArtistListens
import ro.sopa.lastfm.service.dto.ArtistTimelineEntry
import ro.sopa.lastfm.service.dto.ArtistTimelinePoint
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class TrackListenService(val trackListenRepository: TrackListenRepository) {

    @Value("\${dbType}")
    private lateinit var dbType: String

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun findTopArtists(username: String, number: Int): List<ArtistListens> {
        val artistsAndCounts: List<ArtistCountDto> = trackListenRepository.findTopArtists(username, number, dbType)
        return artistsAndCounts.map { dto -> toListens(dto) }
    }

    fun findArtistsTimeline(
        username: String,
        from: ZonedDateTime?,
        to: ZonedDateTime?,
        nrSteps: Int,
        nrArtists: Int
    ): List<ArtistTimelineEntry> {

        var fromFinal = from ?: trackListenRepository.findOldestListenDate(username) ?: ZonedDateTime.now()
        var toFinal: ZonedDateTime = to ?: ZonedDateTime.now()

        val artists: List<ArtistCountDto> = trackListenRepository.findTopArtists(username, nrArtists, dbType)

        val zoneId = fromFinal.zone
        val fromMilis = fromFinal.toInstant().epochSecond
        val toMilis = toFinal.toInstant().epochSecond
        val interval = (toMilis - fromMilis) / nrSteps

        val entries: MutableList<ArtistTimelineEntry> = mutableListOf()

        for (artist in artists) {
            val entry = ArtistTimelineEntry(artist.getArtist())
            val dates = trackListenRepository.findListenDates(username, artist.getArtist(), fromFinal, toFinal)

            // divide the times based on nr of steps
            var currentMilis = fromMilis
            while (currentMilis < toMilis) {
                val min = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentMilis), zoneId)
                val max = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentMilis + interval), zoneId)
                val stepDate = min.format(formatter)

                val count = dates.filter { d -> d.isAfter(min) && d.isBefore(max) }.count().toLong()

                entry.points.add(ArtistTimelinePoint(stepDate, count))
                currentMilis += interval
            }
            entries.add(entry)
        }
        return entries
    }

    private fun toListens(dto: ArtistCountDto): ArtistListens = ArtistListens(dto.getArtist(), dto.getCount())

}