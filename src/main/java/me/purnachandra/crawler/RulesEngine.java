package me.purnachandra.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RulesEngine {
    SimpleRobotRulesParser parser;
    List<String> useragent;

    private final Logger log = LoggerFactory.getLogger(RulesEngine.class);

    public RulesEngine() {
        parser = new SimpleRobotRulesParser();
        useragent = List.of("funfetchbot");
    }

    public boolean isAllowed(String url) {
        String robotUrl = UrlProcessor.getRobotUrl(url);
        byte[] content = new RobotsFetcher().fetch(robotUrl);
        BaseRobotRules rules = parser.parseContent(robotUrl, content, "text/plain", useragent);
        boolean allowed = rules.isAllowed(url);
        log.info("Rules check for url:{} allowed:{}", url, allowed);
        return allowed;
    }
}
