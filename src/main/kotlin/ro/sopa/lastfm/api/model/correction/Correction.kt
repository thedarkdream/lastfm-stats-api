package ro.sopa.lastfm.api.model.correction
import com.google.gson.annotations.SerializedName
import ro.sopa.lastfm.api.model.correction.Artist
import ro.sopa.lastfm.api.model.correction.Attr


data class Correction (

    @SerializedName("artist" ) var artist : Artist? = Artist(),
    @SerializedName("@attr"  ) var attr  : Attr?  = Attr()

)