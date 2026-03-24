package me.purnachandra.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlProcessor {
    private UrlProcessor() {
        /* This utility class should not be instantiated */
    }

    private static final Logger log = LoggerFactory.getLogger(UrlProcessor.class);

    public static String normalize(String urlString) {
        try {
            URI url = new URI(urlString);
            URI uri = new URI(
                    url.getScheme(),
                    url.getUserInfo(),
                    url.getHost().toLowerCase().replaceFirst("^www\\.", ""),
                    removeDefaultPort(url.getScheme(), url.getPort()),
                    removeTrailingSlashes(url.getPath()),
                    cleanQueryParam(url.getQuery()),
                    null // Remove fragment
            );
            return uri.toString();
        } catch (Exception e) {
            log.debug("Skipping invalid URL: {} - {}", urlString, e.getMessage());
            return null;
        }
    }

    private static String cleanQueryParam(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        String result = Arrays.stream(query.split("&"))
                .filter(param -> !param.startsWith("utm_"))
                .filter(param -> !param.startsWith("fbclid"))
                .sorted()
                .collect(Collectors.joining("&"));

        return result.isEmpty() ? null : result;
    }

    private static int removeDefaultPort(String scheme, int port) {
        return scheme.equals("http") && port == 80 || scheme.equals("https") && port == 443 ? -1 : port;
    }

    private static String removeTrailingSlashes(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getRobotUrl(String urlString) {
        try {
            URI url = new URI(urlString);
            URI uri = new URI(
                    url.getScheme(),
                    url.getUserInfo(),
                    url.getHost().toLowerCase().replaceFirst("^www\\.", ""),
                    removeDefaultPort(url.getScheme(), url.getPort()),
                    "/robots.txt",
                    null,
                    null);
            return uri.toString();
        } catch (URISyntaxException e) {
            log.debug("Failed to create robot url from url:{} - {}", urlString, e.getMessage());
        }

        return null;
    }

    public static String extractScheme(String urlString) {
        try {
            URI url = new URI(urlString);
            return url.getScheme();
        } catch (URISyntaxException e) {
            log.debug("Failed to extract protocol from URL:{} - {}", urlString, e.getMessage());
        }

        return null;
    }
}
