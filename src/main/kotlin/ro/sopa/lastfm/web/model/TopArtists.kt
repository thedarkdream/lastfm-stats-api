package ro.sopa.lastfm.web.model


data class ArtistListensObj (val name: String, val listens: Int)

data class TopArtists(val artists: List<ArtistListensObj>)
