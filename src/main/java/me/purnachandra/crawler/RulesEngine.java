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

    public RulesEngine() {
        parser = new SimpleRobotRulesParser();
        useragent = List.of("funfetchbot");
        robotsFetcher = new RobotsFetcher();
        rulesCache = new ConcurrentHashMap<>();
    }

    public boolean isAllowed(String url) {
        String robotUrl = UrlProcessor.getRobotUrl(url);
        BaseRobotRules rules = getBaseRobotRules(robotUrl);
        boolean allowed = rules.isAllowed(url);
        log.info("Rules check for url:{} allowed:{}", url, allowed);
        return allowed;
    }

    private BaseRobotRules getBaseRobotRules(String robotUrl) {
        if (rulesCache.containsKey(robotUrl)) {
            return rulesCache.get(robotUrl);
        }

        byte[] robotContent = robotsFetcher.fetch(robotUrl);
        BaseRobotRules robotRules = parser.parseContent(robotUrl, robotContent, "text/plain", useragent);
        rulesCache.put(robotUrl, robotRules);
        return robotRules;
    }
}
