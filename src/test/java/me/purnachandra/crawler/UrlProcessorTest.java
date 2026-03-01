package me.purnachandra.crawler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UrlProcessorTest {

    @Test
    void removeTrailingSlash() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://example.com/page/"));
    }

    @Test
    void removeFragment() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://example.com/page#section"));
    }

    @Test
    void removeUtmParameters() {
        assertEquals("https://example.com/page",
                UrlProcessor.process("https://example.com/page?utm_source=twitter&utm_medium=social"));
    }

    @Test
    void removeFbclid() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://example.com/page?fbclid=abc123"));
    }

    @Test
    void lowercaseUrl() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://EXAMPLE.COM/page"));
    }

    @Test
    void removeWww() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://www.example.com/page"));
    }

    @Test
    void removeDefaultHttpPort() {
        assertEquals("http://example.com/page", UrlProcessor.process("http://example.com:80/page"));
    }

    @Test
    void removeDefaultHttpsPort() {
        assertEquals("https://example.com/page", UrlProcessor.process("https://example.com:443/page"));
    }

    @Test
    void sortQueryParameters() {
        assertEquals("https://example.com/page?a=1&b=2", UrlProcessor.process("https://example.com/page?b=2&a=1"));
    }

    @Test
    void removeFragmentButKeepQueryParams() {
        assertEquals("https://example.com/page?q=search",
                UrlProcessor.process("https://example.com/page?q=search#section"));
    }
}
