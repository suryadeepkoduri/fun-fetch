package me.purnachandra.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotsFetcher {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Logger log = LoggerFactory.getLogger(RobotsFetcher.class);

    public byte[] fetch(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("User-Agent", "FunFetchBot")
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofByteArray()).body();
        } catch (IOException e) {
            log.error("IOException while fetching robots.txt from url:{}", url, e);
        } catch (InterruptedException e) {
            log.error("InterruptedException while fetching robots.txt from url:{}", url, e);
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while fetching robots.txt from url:{}", url, e);
        }
        return new byte[0];
    }
}
