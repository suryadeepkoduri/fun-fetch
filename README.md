# FunFetch

A search engine built from scratch in Java — crawler, indexer, and search API.

## What it does

FunFetch crawls the web, indexes pages, and lets you search them via a REST API.

- **Crawler** — BFS web crawler using Jsoup, respects crawl depth and politeness delays
- **Indexer** — tokenizes page content into an inverted index with TF-IDF scoring
- **Search API** — Spring Boot REST endpoint returning ranked results

## Stack

Java · Spring Boot · PostgreSQL · HikariCP

## Status

Active development — v1 in progress.
