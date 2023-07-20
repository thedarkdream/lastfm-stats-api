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

    @Query("select max(t.date) from TrackListen t where t.userId=:userId")
    fun findLatestListenDate(@Param("userId") userId: Int): ZonedDateTime?

    @Query("select min(t.date) from TrackListen t where t.userId=:userId")
    fun findOldestListenDate(@Param("userId") userId: Int): ZonedDateTime?

    @Query(value = "select t.artist_id as artistId, count(1) as count from track_Listens t where t.user_Id = :userId group by t.artist_id order by count desc limit :number", nativeQuery = true)
    fun findTopArtists_mysql(@Param("userId") userId: Int, @Param("number") number: Int?): List<ArtistCountDto>

    @Query(value = "select t.artist_id as artistId, count(1) as count from track_Listens t where t.user_Id = :userId and t.date >= :from and t.date <= :to group by t.artist_id order by count desc limit :number", nativeQuery = true)
    fun findTopArtists_mysql(@Param("userId") userId: Int, @Param("number") number: Int?, @Param("from") from: ZonedDateTime, @Param("to") to: ZonedDateTime): List<ArtistCountDto>

    @Query(value = "select top (:number) t.artist_id as artistId, count(1) as count from track_Listens t where t.user_Id = :userId group by t.artist_id order by count desc", nativeQuery = true)
    fun findTopArtists_mssql(@Param("userId") userId: Int, @Param("number") number: Int?): List<ArtistCountDto>

    @Query(value = "select top (:number) t.artist_id as artistId, count(1) as count from track_Listens t where t.user_Id = :userId and t.date >= :from and t.date <= :to group by t.artist_id order by count desc", nativeQuery = true)
    fun findTopArtists_mssql(@Param("userId") userId: Int, @Param("number") number: Int?, @Param("from") from: ZonedDateTime, @Param("to") to: ZonedDateTime): List<ArtistCountDto>

    @Query("select t.date from TrackListen t where t.userId = :userId and artistId = :artistId and date >= :minDate and date <= :maxDate")
    fun findListenDates(
        @Param("userId") userId: Int,
        @Param("artistId") artist: Int,
        @Param("minDate") minDate: ZonedDateTime?,
        @Param("maxDate") maxDate: ZonedDateTime?
    ): List<ZonedDateTime>

}

object TrackListenRepositoryExtensions {

    fun TrackListenRepository.findTopArtists(userId: Int, number: Int, dbType: String): List<ArtistCountDto> {
        if ("mysql" == dbType) {
            return findTopArtists_mysql(userId, number)
        }
        if ("mssql" == dbType) {
            return findTopArtists_mssql(userId, number)
        }
        throw RuntimeException("Unknown db type: $dbType")
    }

    fun TrackListenRepository.findTopArtists(userId: Int, number: Int, dbType: String, from: ZonedDateTime, to: ZonedDateTime): List<ArtistCountDto> {
        if ("mysql" == dbType) {
            return findTopArtists_mysql(userId, number, from, to)
        }
        if ("mssql" == dbType) {
            return findTopArtists_mssql(userId, number, from, to)
        }
        throw RuntimeException("Unknown db type: $dbType")
    }

}