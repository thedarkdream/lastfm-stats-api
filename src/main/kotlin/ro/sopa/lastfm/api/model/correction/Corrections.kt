package ro.sopa.lastfm.api.model.correction

import com.google.gson.annotations.SerializedName


data class Corrections (

  @SerializedName("correction" ) var correction : Correction? = Correction()

)