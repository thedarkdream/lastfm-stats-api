package ro.sopa.lastfm.controller

import org.springframework.web.bind.annotation.*
import ro.sopa.lastfm.service.TrackListenService
import ro.sopa.lastfm.service.dto.ArtistListens
import ro.sopa.lastfm.service.dto.ArtistTimelineEntry
import ro.sopa.lastfm.service.dto.ArtistTimelinePoint
import ro.sopa.lastfm.web.model.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RestController
class StatisticsController(
    val trackListenService: TrackListenService
) {

    @GetMapping("/{username}/artists")
    fun artists(@PathVariable username: String, @RequestParam(required = false) number: Int?): TopArtists? {
        val artistListens: List<ArtistListens> = trackListenService.findTopArtists(username,number ?: 20)
        return TopArtists(artistListens.map { artistListens -> toArtist(artistListens) })
    }

    @GetMapping("/{username}/artists/timeline")
    fun artistTimeline(
        @PathVariable username: String,
        @RequestParam(required = false) dateFrom: String?,
        @RequestParam(required = false) dateTo: String?,
        @RequestParam(required = false) nrSteps: Int?,
        @RequestParam(required = false) nrArtists: Int?
    ): ArtistsTimeline {
        val startDate = if (dateFrom != null) ZonedDateTime.parse(dateFrom, DateTimeFormatter.ISO_DATE) else null
        val endDate = if (dateFrom != null) ZonedDateTime.parse(dateTo, DateTimeFormatter.ISO_DATE) else null
        val entries: List<ArtistTimelineEntry> = trackListenService.findArtistsTimeline(
            username, startDate, endDate,
            nrSteps ?: 30,
            nrArtists ?: 10
        )

        return ArtistsTimeline(entries.map { entry -> mapTimeline(entry) })
    }

    private fun mapTimeline(e: ArtistTimelineEntry): ArtistTimeline = ArtistTimeline(e.artist, mapPoints(e.points))

    private fun mapPoints(points: List<ArtistTimelinePoint>): List<TimelinePoint> = points.map { p -> TimelinePoint(p.time, p.listens) }

    private fun toArtist(artistListens: ArtistListens): ArtistListensObj = ArtistListensObj(artistListens.artist, artistListens.count)

}