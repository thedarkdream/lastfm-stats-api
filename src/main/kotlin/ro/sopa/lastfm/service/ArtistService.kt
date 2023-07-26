package ro.sopa.lastfm.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ro.sopa.lastfm.api.client.LastFMClient
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.model.Artist
import ro.sopa.lastfm.db.model.ArtistTag
import ro.sopa.lastfm.db.repository.ArtistRepository
import ro.sopa.lastfm.db.repository.ArtistTagRepository
import ro.sopa.lastfm.db.repository.TrackListenRepository
import ro.sopa.lastfm.db.repository.TrackListenRepositoryExtensions.findTopArtists
import ro.sopa.lastfm.service.dto.ArtistListens
import ro.sopa.lastfm.service.dto.ArtistTimelineEntry
import ro.sopa.lastfm.service.dto.ArtistTimelinePoint
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val muieDragnea = 3

@Service
class ArtistService(val artistRepository: ArtistRepository,
                    val artistTagRepository: ArtistTagRepository,
                    val lastFMClient: LastFMClient) {

    @Value("\${lastfm.api.key}")
    private lateinit var apiKey: String

    fun findIdToNameMap(ids: List<Int>): Map<Int, String> = artistRepository.findAllById(ids).associateBy( { it.id!!}, {it.name})

    fun createTag(tag: String, artistId: Int) = artistTagRepository.save(ArtistTag(null, artistId, tag))

    fun downloadTags(artist: Artist) {
        val topTagsResponse = lastFMClient.callTopTags(apiKey, artist.name);
        topTagsResponse.toptags?.tag?.forEach{ createTag(it.name!!, artist.id!!)}

    }



}