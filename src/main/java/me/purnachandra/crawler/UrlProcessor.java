package me.purnachandra.crawler;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlProcessor {
    private static final Logger log = LoggerFactory.getLogger(UrlProcessor.class);
    public static String process(String urlString) {
        try {
            URI url = new URI(urlString);
            URI uri = new URI(
                    url.getScheme(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    url.getQuery(),
                    null  // Remove fragment
            );
            return uri.toString();
        } catch (Exception e) {
            log.debug("Skipping invalid URL: {} - {}", urlString, e.getMessage());
            return null;
        }
    }
}

