package me.purnachandra.index;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class IndexOrchestrator {
    private final Indexer indexer;
    private final IndexRepository indexRepository;
    Queue<Integer> localQueue = new LinkedList<>();

    public IndexOrchestrator(Indexer indexer, IndexRepository indexRepository) {
        this.indexer = indexer;
        this.indexRepository = indexRepository;
    }

    public void start() {
        while (true) {
            if (localQueue.isEmpty()) {
                List<Integer> batch = indexRepository.getNextPendingBatch(100);
                if (batch.isEmpty()) {
                    break;
                }
                localQueue.addAll(batch);
            }

            int pageId = localQueue.poll();
            Map<String, Integer> freqs = indexer.index(indexRepository.fetchPageContent(pageId));

            indexRepository.addIndex(pageId, freqs);
        }
    }
}
