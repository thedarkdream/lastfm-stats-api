package ro.sopa.lastfm.db

interface ArtistCountDto {
    fun getArtist(): String
    fun getCount(): Int
}