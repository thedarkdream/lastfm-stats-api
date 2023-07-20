package ro.sopa.lastfm.db.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.model.Artist
import ro.sopa.lastfm.db.model.ArtistAlias
import ro.sopa.lastfm.db.model.TrackListen
import java.time.ZonedDateTime

@Repository
interface ArtistAliasRepository : CrudRepository<ArtistAlias, Int>{

    @Query("select a.artistId from ArtistAlias a where a.name=:name")
    fun findArtistIdByAlias(@Param("name") name: String): Int?

}