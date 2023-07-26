package ro.sopa.lastfm.api.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.google.gson.Gson
import org.springframework.stereotype.Component
import ro.sopa.lastfm.api.model.ListeningHistory
import ro.sopa.lastfm.api.model.correction.CorrectionResponse
import ro.sopa.lastfm.api.model.toptags.TopTagsResponse
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

@Component
class LastFMClient {

    private val gson = Gson()

    private val pageSize: Int = 200

    private val recentTracksUrl = "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&api_key=%s&format=json&page=%s&limit=%s"
    private val correctionsUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.getcorrection&artist=%s&api_key=%s&format=json"
    private val topTagsUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptags&artist=%s&api_key=%s&format=json"

    fun callRecentTracks(apiKey: String, username: String, page: Int): ListeningHistory {

        val client = HttpClient.newHttpClient()
        val url = String.format(recentTracksUrl, username, apiKey, page, pageSize)

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

    fun callCorrections(apiKey: String, artist: String): CorrectionResponse {

        val client = HttpClient.newHttpClient()
        val url = String.format(correctionsUrl, URLEncoder.encode(artist, StandardCharsets.UTF_8.toString()), apiKey)
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
                    return gson.fromJson(responseBody, CorrectionResponse::class.java)
                } catch (e: Exception) {
                    throw RuntimeException("Can't deserialise response!", e)
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

    fun callTopTags(apiKey: String, artist: String): TopTagsResponse {
        val client = HttpClient.newHttpClient()
        val url = String.format(topTagsUrl, URLEncoder.encode(artist, StandardCharsets.UTF_8.toString()), apiKey)
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
                    return gson.fromJson(responseBody, TopTagsResponse::class.java)
                } catch (e: Exception) {
                    throw RuntimeException("Can't deserialise response!", e)
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