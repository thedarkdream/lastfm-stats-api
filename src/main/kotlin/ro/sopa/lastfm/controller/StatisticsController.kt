package ro.sopa.lastfm.controller

import org.springframework.web.bind.annotation.*
import ro.sopa.lastfm.db.repository.UserRepository
import ro.sopa.lastfm.service.ArtistService
import ro.sopa.lastfm.service.TrackListenService
import ro.sopa.lastfm.service.dto.ArtistListens
import ro.sopa.lastfm.service.dto.ArtistTimelineEntry
import ro.sopa.lastfm.service.dto.ArtistTimelinePoint
import ro.sopa.lastfm.web.model.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RestController
class StatisticsController(
    val trackListenService: TrackListenService,
    val artistService: ArtistService,
    val userRepository: UserRepository
) {

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/{username}/artists")
    fun artists(@PathVariable username: String,
                @RequestParam(required = false) number: Int?,
                @RequestParam(required = false) dateFrom: String?,
                @RequestParam(required = false) dateTo: String?,
    ): TopArtists? {
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("Unknown user!")
        val artistListens: List<ArtistListens> = trackListenService.findTopArtists(user?.id!!,number ?: 20)
        val artistIdToNameMap = artistService.findIdToNameMap(artistListens.map { listen -> listen.artistId })
        return TopArtists(artistListens.map { artistListens -> toArtist(artistListens, artistIdToNameMap) })
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/{username}/artists/timeline")
    fun artistTimeline(
        @PathVariable username: String,
        @RequestParam(required = false) dateFrom: String?,
        @RequestParam(required = false) dateTo: String?,
        @RequestParam(required = false) nrSteps: Int?,
        @RequestParam(required = false) nrArtists: Int?
    ): ArtistsTimeline {
        val startDate = if (dateFrom != null) ZonedDateTime.parse(dateFrom, DateTimeFormatter.ISO_ZONED_DATE_TIME) else null
        val endDate = if (dateFrom != null) ZonedDateTime.parse(dateTo, DateTimeFormatter.ISO_ZONED_DATE_TIME) else null

        val user = userRepository.findByUsername(username) ?: throw RuntimeException("Unknown user!")

        val entries: List<ArtistTimelineEntry> = trackListenService.findArtistsTimeline(
            user?.id!!, startDate, endDate,
            nrSteps ?: 30,
            nrArtists ?: 10
        )

        return ArtistsTimeline(entries.map { entry -> mapTimeline(entry) })
    }

    private fun mapTimeline(e: ArtistTimelineEntry): ArtistTimeline = ArtistTimeline(e.artist, mapPoints(e.points))

    private fun mapPoints(points: List<ArtistTimelinePoint>): List<TimelinePoint> = points.map { p -> TimelinePoint(p.time, p.listens) }

    private fun toArtist(artistListens: ArtistListens, artistIdToNameMap: Map<Int, String>): ArtistListensObj = ArtistListensObj(artistIdToNameMap[artistListens.artistId]!!, artistListens.count)

}