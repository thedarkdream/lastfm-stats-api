package ro.sopa.lastfm.db.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "track_listens")
data class TrackListen (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Int? = null,
    var userId: Int,
    var artistId: Int,
    var album: String? = null,
    var track: String,
    var albumId: String? = null,
    var date: ZonedDateTime? = null

)