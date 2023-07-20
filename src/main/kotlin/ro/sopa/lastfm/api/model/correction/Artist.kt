package ro.sopa.lastfm.api.model.correction

import com.google.gson.annotations.SerializedName


data class Artist (

  @SerializedName("name" ) var name : String? = null,
  @SerializedName("mbid" ) var mbid : String? = null,
  @SerializedName("url"  ) var url  : String? = null

)