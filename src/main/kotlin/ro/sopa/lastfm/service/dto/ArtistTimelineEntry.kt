package ro.sopa.lastfm.service.dto

data class ArtistTimelineEntry(
    val artist: String
) {
    var points: MutableList<ArtistTimelinePoint> = mutableListOf();
}
