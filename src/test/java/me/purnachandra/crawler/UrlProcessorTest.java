package me.purnachandra.crawler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UrlProcessorTest {

    @Test
    void removeTrailingSlash() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://example.com/page/"));
    }

    @Test
    void removeFragment() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://example.com/page#section"));
    }

    @Test
    void removeUtmParameters() {
        assertEquals("https://example.com/page",
                UrlProcessor.normalize("https://example.com/page?utm_source=twitter&utm_medium=social"));
    }

    @Test
    void removeFbclid() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://example.com/page?fbclid=abc123"));
    }

    @Test
    void lowercaseUrl() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://EXAMPLE.COM/page"));
    }

    @Test
    void removeWww() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://www.example.com/page"));
    }

    @Test
    void removeDefaultHttpPort() {
        assertEquals("http://example.com/page", UrlProcessor.normalize("http://example.com:80/page"));
    }

    @Test
    void removeDefaultHttpsPort() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://example.com:443/page"));
    }

    @Test
    void sortQueryParameters() {
        assertEquals("https://example.com/page?a=1&b=2", UrlProcessor.normalize("https://example.com/page?b=2&a=1"));
    }

    @Test
    void removeFragmentButKeepQueryParams() {
        assertEquals("https://example.com/page?q=search",
                UrlProcessor.normalize("https://example.com/page?q=search#section"));
    }

    @Test
    void keepNonDefaultPort() {
        assertEquals("http://example.com:8080/page", UrlProcessor.normalize("http://example.com:8080/page"));
    }

    @Test
    void normalize_nullInput_returnsNull() {
        assertNull(UrlProcessor.normalize(null));
    }

    @Test
    void normalize_malformedUrl_returnsNull() {
        assertNull(UrlProcessor.normalize("not a url"));
    }

    @Test
    void normalize_removesUtmButKeepsOtherParams() {
        assertEquals(
                "https://example.com/page?q=search",
                UrlProcessor.normalize("https://example.com/page?q=search&utm_source=twitter"));
    }

    @Test
    void normalize_removesFbclidButKeepsOtherParams() {
        assertEquals(
                "https://example.com/page?q=search",
                UrlProcessor.normalize("https://example.com/page?q=search&fbclid=abc123"));
    }

    @Test
    void normalize_multipleTrailingSlashes() {
        assertEquals("https://example.com/page", UrlProcessor.normalize("https://example.com/page///"));
    }

    @Test
    void getRobotUrl_returnsRobotsTextPath() {
        assertEquals("https://example.com/robots.txt", UrlProcessor.getRobotUrl("https://example.com/some/page"));
    }

    @Test
    void getRobotUrl_stripsWww() {
        assertEquals("https://example.com/robots.txt", UrlProcessor.getRobotUrl("https://www.example.com/page"));
    }

    @Test
    void getRobotUrl_lowercasesHost() {
        assertEquals("https://example.com/robots.txt", UrlProcessor.getRobotUrl("https://EXAMPLE.COM/page"));
    }

    @Test
    void getRobotUrl_stripsQueryParams() {
        assertEquals("https://example.com/robots.txt", UrlProcessor.getRobotUrl("https://example.com/page?q=search"));
    }

    @Test
    void getRobotUrl_stripsFragment() {
        assertEquals("https://example.com/robots.txt", UrlProcessor.getRobotUrl("https://example.com/page#section"));
    }

    @Test
    void getRobotUrl_sameDomainDifferentPaths_returnsSameRobotsUrl() {
        assertEquals(
                UrlProcessor.getRobotUrl("https://example.com/page1"),
                UrlProcessor.getRobotUrl("https://example.com/page2"));
    }
}
