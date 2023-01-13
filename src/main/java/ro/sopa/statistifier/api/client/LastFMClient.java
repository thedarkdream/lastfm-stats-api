package ro.sopa.statistifier.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import ro.sopa.statistifier.api.model.ListeningHistory;

@Component
public class LastFMClient {

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL = "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&api_key=%s&format=json&page=%s";

    public ListeningHistory callRecentTracks(String apiKey, String username, int page) {

        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .build();

        String url = String.format(URL, username, apiKey, page);

        RequestBody requestBody = RequestBody.create(null, new byte[0]);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response;

        try {
            response = httpClient.newCall(request).execute();
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred during file transfer!", t);
        }

        if (response.code() == 200) {
            String responseBody = response.body().toString();
            try {
                return objectMapper.readValue(responseBody, ListeningHistory.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't deserialise response!");
            }

        } else {
            throw new RuntimeException("Can't call endpoint, received " + response.code() + ". Mesage is " + response.body().toString());
        }
    }
}
