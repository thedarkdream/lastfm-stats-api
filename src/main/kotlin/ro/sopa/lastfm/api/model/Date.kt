package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Date (

  @SerializedName("uts"   ) var uts   : String? = null,
  @SerializedName("#text" ) var text : String? = null

)