package ro.sopa.lastfm.web.model

data class ArtistsTimeline(val artistTimelines: List<ArtistTimeline>)

data class ArtistTimeline(val artist: String, val points: List<TimelinePoint>)

data class TimelinePoint (val time: String, val listens: Long)
