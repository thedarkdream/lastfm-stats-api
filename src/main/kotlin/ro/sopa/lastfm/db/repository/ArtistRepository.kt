package ro.sopa.lastfm.db.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.model.Artist
import ro.sopa.lastfm.db.model.TrackListen
import java.time.ZonedDateTime

@Repository
interface ArtistRepository : CrudRepository<Artist, Int>{

    fun findByName(@Param("name") name: String) : Artist?
}