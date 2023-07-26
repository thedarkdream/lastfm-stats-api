package ro.sopa.lastfm.api.model.toptags

import com.google.gson.annotations.SerializedName


data class Tag (

  @SerializedName("count" ) var count : Int?    = null,
  @SerializedName("name"  ) var name  : String? = null,
  @SerializedName("url"   ) var url   : String? = null

)