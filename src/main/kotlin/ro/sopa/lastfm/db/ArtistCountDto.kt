package ro.sopa.lastfm.db

interface ArtistCountDto {
    fun getArtistId(): Int
    fun getCount(): Int
}