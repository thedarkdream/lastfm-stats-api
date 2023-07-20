package ro.sopa.lastfm.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.repository.ArtistRepository
import ro.sopa.lastfm.db.repository.TrackListenRepository
import ro.sopa.lastfm.db.repository.TrackListenRepositoryExtensions.findTopArtists
import ro.sopa.lastfm.service.dto.ArtistListens
import ro.sopa.lastfm.service.dto.ArtistTimelineEntry
import ro.sopa.lastfm.service.dto.ArtistTimelinePoint
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class ArtistService(val artistRepository: ArtistRepository) {
    fun findIdToNameMap(ids: List<Int>): Map<Int, String> {
        return artistRepository.findAllById(ids).associateBy( {it.id?.toInt()!!}, {it.name} )
    }


}