package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Album (

  @SerializedName("mbid"  ) var mbid  : String? = null,
  @SerializedName("#text" ) var text : String? = null

)