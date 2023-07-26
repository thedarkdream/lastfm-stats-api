package ro.sopa.lastfm.api.model.toptags

import com.google.gson.annotations.SerializedName


data class Attr (

  @SerializedName("artist" ) var artist : String? = null

)