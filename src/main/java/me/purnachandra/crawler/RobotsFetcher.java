package me.purnachandra.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotsFetcher {
    private final Logger log = LoggerFactory.getLogger(RobotsFetcher.class);

    public byte[] fetch(String url) {
        HttpURLConnection conn;
        try {
            URI uri = new URI(url);
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "FunFetchBot/1.0");

            InputStream is = conn.getInputStream();
            byte[] content = is.readAllBytes();
            is.close();
            return content;
        } catch (MalformedURLException e) {
            log.error("Invalid URL for robots.txt: {}", url);
        } catch (IOException e) {
            log.error("Error fetching robots.txt from {}: {}", url, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching robots.txt from {}: {}", url, e.getMessage());
        }

        return new byte[0];
    }
}
