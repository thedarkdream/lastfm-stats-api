package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Recenttracks (

  @SerializedName("track" ) var track : ArrayList<Track> = arrayListOf(),
  @SerializedName("@attr" ) var attr : Attr?           = Attr()

)