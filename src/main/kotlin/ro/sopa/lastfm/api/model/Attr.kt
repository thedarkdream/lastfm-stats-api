package ro.sopa.lastfm.api.model

import com.google.gson.annotations.SerializedName


data class Attr (

  @SerializedName("user"       ) var user       : String? = null,
  @SerializedName("totalPages" ) var totalPages : String? = null,
  @SerializedName("page"       ) var page       : String? = null,
  @SerializedName("total"      ) var total      : String? = null,
  @SerializedName("perPage"    ) var perPage    : String? = null

)