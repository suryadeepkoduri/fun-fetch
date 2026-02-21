package me.purnachandra.search;

public class SearchResult {
    private final int docId;
    private final String url;
    private final String title;
    private final double score;

    public SearchResult(int docId, String url, String title, double score) {
        this.docId = docId;
        this.url = url;
        this.title = title;
        this.score = score;
    }

    public int getDocId() {
        return docId;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public double getScore() {
        return score;
    }

}
