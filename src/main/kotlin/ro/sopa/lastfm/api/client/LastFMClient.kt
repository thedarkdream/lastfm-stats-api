package ro.sopa.lastfm.api.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.stereotype.Component
import ro.sopa.lastfm.api.model.ListeningHistory
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class LastFMClient {

    private val gson = Gson()

    private val pageSize: Int = 200

    private val apiUrl = "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&api_key=%s&format=json&page=%s&limit=%s"

    fun callRecentTracks(apiKey: String?, username: String?, page: Int): ListeningHistory {

        val client = HttpClient.newHttpClient()
        val url = String.format(apiUrl, username, apiKey, page, pageSize)

        var request: HttpRequest?

        return try {
            request = HttpRequest.newBuilder()
                .uri(URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                val responseBody = response.body().toString()
                try {
                    gson.fromJson(responseBody, ListeningHistory::class.java)
                } catch (e: JsonProcessingException) {
                    throw RuntimeException("Can't deserialise response!")
                }
            } else {
                throw RuntimeException("Can't call endpoint, received " + response.statusCode() + ". Mesage is " + response.body().toString())
            }

        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}