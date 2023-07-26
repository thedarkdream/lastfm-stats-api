package ro.sopa.lastfm.api.model.toptags

import com.google.gson.annotations.SerializedName


data class TopTagsResponse (

  @SerializedName("toptags" ) var toptags : Toptags? = Toptags()

)