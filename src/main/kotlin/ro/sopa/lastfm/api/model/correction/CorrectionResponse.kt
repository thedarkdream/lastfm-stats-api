package ro.sopa.lastfm.api.model.correction

import com.google.gson.annotations.SerializedName


data class CorrectionResponse (

  @SerializedName("corrections" ) var corrections : Corrections? = Corrections()

)