package ro.sopa.statistifier.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ro.sopa.statistifier.api.model.ListeningHistory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class LastFMClient {

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final int PAGE_SIZE = 200;

    private static final String URL = "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&api_key=%s&format=json&page=%s&limit=%s";

    public ListeningHistory callRecentTracks(String apiKey, String username, int page) {

        HttpClient client = HttpClient.newHttpClient();

        String url = String.format(URL, username, apiKey, page, PAGE_SIZE);

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body().toString();
                try {
                    return objectMapper.readValue(responseBody, ListeningHistory.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Can't deserialise response!");
                }

            } else {
                throw new RuntimeException("Can't call endpoint, received " + response.statusCode() + ". Mesage is " + response.body().toString());
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
