package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class ListeningHistory (

  @SerializedName("recenttracks" ) var recenttracks : Recenttracks? = Recenttracks()

)