package me.purnachandra.crawler;

import java.net.URI;

public class UrlProcessor {
    public static String process(String urlString) {
        try {
            URI url = new URI(urlString);
            URI uri = new URI(
                    url.getScheme(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    null, // Remove query
                    null  // Remove fragment
            );
            return uri.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

