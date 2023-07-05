package ro.sopa.lastfm.db.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ro.sopa.lastfm.db.ArtistCountDto
import ro.sopa.lastfm.db.model.TrackListen
import java.time.ZonedDateTime

@Repository
interface TrackListenRepository : CrudRepository<TrackListen, Int>{

    @Query("select max(t.date) from TrackListen t where t.username=:username")
    fun findLatestListenDate(@Param("username") username: String?): ZonedDateTime?

    @Query("select min(t.date) from TrackListen t where t.username=:username")
    fun findOldestListenDate(@Param("username") username: String?): ZonedDateTime?

    @Query(value = "select t.artist as artist, count(1) as count from Track_Listen t where t.username = :username group by t.artist order by count desc limit :number", nativeQuery = true)
    fun findTopArtists_mysql(@Param("username") username: String?, @Param("number") number: Int?): List<ArtistCountDto>

    @Query(
        value = "select top (:number) t.artist as artist, count(1) as count from Track_Listen t where t.username = :username group by t.artist order by count desc",
        nativeQuery = true
    )
    fun findTopArtists_mssql(@Param("username") username: String?, @Param("number") number: Int?): List<ArtistCountDto>

    @Query("select t.date from TrackListen t where t.username = :username and artist = :artist and date >= :minDate and date <= :maxDate")
    fun findListenDates(
        @Param("username") username: String,
        @Param("artist") artist: String,
        @Param("minDate") minDate: ZonedDateTime?,
        @Param("maxDate") maxDate: ZonedDateTime?
    ): List<ZonedDateTime>

}

object TrackListenRepositoryExtensions {

    fun TrackListenRepository.findTopArtists(username: String, number: Int, dbType: String): List<ArtistCountDto> {
        if ("mysql" == dbType) {
            return findTopArtists_mysql(username, number)
        }
        if ("mssql" == dbType) {
            return findTopArtists_mssql(username, number)
        }
        throw RuntimeException("Unknown db type: $dbType")
    }

}