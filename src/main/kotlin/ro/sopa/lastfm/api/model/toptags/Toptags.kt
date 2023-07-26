package ro.sopa.lastfm.api.model.toptags

import com.google.gson.annotations.SerializedName


data class Toptags (

  @SerializedName("tag"   ) var tag   : ArrayList<Tag> = arrayListOf(),
  @SerializedName("@attr" ) var attr : Attr?         = Attr()

)