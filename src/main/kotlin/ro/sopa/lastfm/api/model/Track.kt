package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Track (

  @SerializedName("artist"     ) var artist     : Artist?          = Artist(),
  @SerializedName("streamable" ) var streamable : String?          = null,
  @SerializedName("image"      ) var image      : ArrayList<Image> = arrayListOf(),
  @SerializedName("mbid"       ) var mbid       : String?          = null,
  @SerializedName("album"      ) var album      : Album?           = Album(),
  @SerializedName("name"       ) var name       : String?          = null,
  @SerializedName("url"        ) var url        : String?          = null,
  @SerializedName("date"       ) var date       : Date?            = Date()

)