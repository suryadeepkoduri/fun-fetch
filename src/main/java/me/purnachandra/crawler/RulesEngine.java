package me.purnachandra.crawler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RulesEngine {
    private final SimpleRobotRulesParser parser;
    private final List<String> useragent;
    private final RobotsFetcher robotsFetcher;
    private ConcurrentHashMap<String, BaseRobotRules> rulesCache;

    private final Logger log = LoggerFactory.getLogger(RulesEngine.class);

    public RulesEngine(RobotsFetcher robotsFetcher) {
        parser = new SimpleRobotRulesParser();
        useragent = List.of("funfetchbot");
        this.robotsFetcher = robotsFetcher;
        rulesCache = new ConcurrentHashMap<>();
    }

    public boolean isAllowed(String url) {
        String robotUrl = UrlProcessor.getRobotUrl(url);

        if (robotUrl == null) {
            return false;
        }
        
        BaseRobotRules rules = getBaseRobotRules(robotUrl);
        boolean allowed = rules.isAllowed(url);
        log.info("Rules check for url:{} allowed:{}", url, allowed);
        return allowed;
    }

    private BaseRobotRules getBaseRobotRules(String robotUrl) {
        return rulesCache.computeIfAbsent(robotUrl, url -> {
            byte[] robotContent = robotsFetcher.fetch(url);
            return parser.parseContent(url, robotContent, "text/plain", useragent);
        });
    }
}
