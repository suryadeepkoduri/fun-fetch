package me.purnachandra.index;

public class IndexMain {
    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        IndexRepository indexRepository = new IndexRepository();
        IndexOrchestrator orchestrator = new IndexOrchestrator(indexer, indexRepository);
        orchestrator.start();
    }
}
