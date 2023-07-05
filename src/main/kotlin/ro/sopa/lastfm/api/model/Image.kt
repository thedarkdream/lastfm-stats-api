package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Image (

  @SerializedName("size"  ) var size  : String? = null,
  @SerializedName("#text" ) var text : String? = null

)