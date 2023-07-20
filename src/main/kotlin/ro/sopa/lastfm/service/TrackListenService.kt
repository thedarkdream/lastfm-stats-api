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
class TrackListenService(val trackListenRepository: TrackListenRepository, val artistService: ArtistService) {

    @Value("\${dbType}")
    private lateinit var dbType: String

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun findTopArtists(userId: Int, number: Int): List<ArtistListens> {
        val artistsAndCounts: List<ArtistCountDto> = trackListenRepository.findTopArtists(userId, number, dbType)
        return artistsAndCounts.map { dto -> toListens(dto) }
    }

    fun findArtistsTimeline(
        userId: Int,
        from: ZonedDateTime?,
        to: ZonedDateTime?,
        nrSteps: Int,
        nrArtists: Int
    ): List<ArtistTimelineEntry> {

        var fromFinal = from ?: trackListenRepository.findOldestListenDate(userId) ?: ZonedDateTime.now()
        var toFinal: ZonedDateTime = to ?: ZonedDateTime.now()

        val artists: List<ArtistCountDto> = trackListenRepository.findTopArtists(userId, nrArtists, dbType, fromFinal, toFinal)

        val zoneId = fromFinal.zone
        val fromMilis = fromFinal.toInstant().epochSecond
        val toMilis = toFinal.toInstant().epochSecond
        val interval = (toMilis - fromMilis) / nrSteps

        val entries: MutableList<ArtistTimelineEntry> = mutableListOf()

        var artistMap = artistService.findIdToNameMap(artists.map { a -> a.getArtistId() })

        for (artist in artists) {
            val entry = ArtistTimelineEntry(artistMap[artist.getArtistId()]!!)
            val dates = trackListenRepository.findListenDates(userId, artist.getArtistId(), fromFinal, toFinal)

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

    private fun toListens(dto: ArtistCountDto): ArtistListens = ArtistListens(dto.getArtistId(), dto.getCount())

}