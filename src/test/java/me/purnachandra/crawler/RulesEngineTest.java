package me.purnachandra.crawler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RulesEngineTest {

    private RulesEngine rulesEngine;
    private RobotsFetcher mockFetcher;

    @BeforeEach
    void setUp() {
        mockFetcher = Mockito.mock(RobotsFetcher.class);
        rulesEngine = new RulesEngine(mockFetcher);
    }

    @Test
    void disallowedPathIsBlocked() {
        String robotsTxt = "User-agent: *\nDisallow: /private/";
        Mockito.when(mockFetcher.fetch(any()))
               .thenReturn(robotsTxt.getBytes());

        assertFalse(rulesEngine.isAllowed("https://example.com/private/secret"));
    }

    @Test
    void allowedPathPassesThrough() {
        String robotsTxt = "User-agent: *\nDisallow: /private/";
        Mockito.when(mockFetcher.fetch(any()))
               .thenReturn(robotsTxt.getBytes());

        assertTrue(rulesEngine.isAllowed("https://example.com/public/page"));
    }

    @Test
    void emptyRobotsTxtAllowsEverything() {
        Mockito.when(mockFetcher.fetch(any()))
               .thenReturn(new byte[]{});

        assertTrue(rulesEngine.isAllowed("https://example.com/anything"));
    }

    @Test
    void robotsTxtIsCachedAndFetchedOnlyOnce() {
        String robotsTxt = "User-agent: *\nDisallow: /private/";
        Mockito.when(mockFetcher.fetch(any()))
               .thenReturn(robotsTxt.getBytes());

        rulesEngine.isAllowed("https://example.com/page1");
        rulesEngine.isAllowed("https://example.com/page2");

        // same domain — should only fetch robots.txt once
        Mockito.verify(mockFetcher, Mockito.times(1)).fetch(any());
    }

    @Test
    void differentDomainsEachFetchTheirOwnRobotsTxt() {
        String robotsTxt = "User-agent: *\nDisallow:";
        Mockito.when(mockFetcher.fetch(any()))
               .thenReturn(robotsTxt.getBytes());

        rulesEngine.isAllowed("https://example.com/page");
        rulesEngine.isAllowed("https://other.com/page");

        Mockito.verify(mockFetcher, Mockito.times(2)).fetch(any());
    }
}
