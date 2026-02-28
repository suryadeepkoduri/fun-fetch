package me.purnachandra.crawler;

import crawlercommons.robots.SimpleRobotRulesParser;

public class RulesEngine {
    SimpleRobotRulesParser parser;
    String useragent;

    public RulesEngine() {
        parser = new SimpleRobotRulesParser();
        useragent = "*";
    }
}
