package com.d1ff.analyticservice.repository.abstractClass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
abstract public class BatchClickHouseRepository<T> {
    private static final int FLUSH_THRESHOLD = 500;

    private final ConcurrentLinkedQueue<T> buffer = new ConcurrentLinkedQueue<>();

    public void add(T item){
        buffer.add(item);
        if(buffer.size() >= FLUSH_THRESHOLD){
            flush();
        }
    }

    @Scheduled(fixedRate = 50)
    public void scheduledFlush(){
        flush();
    }

    protected synchronized void flush() {
        List<T> batch = new ArrayList<>();
        T item;

        while ((item = buffer.poll()) != null) {
            batch.add(item);
        }

        if (batch.isEmpty()) {
            return;
        }
        try {
            persist(batch);
            log.info("[{}] Flushed {} events to ClickHouse", tableName(), batch.size());
        } catch (Exception e) {
            buffer.addAll(batch);
            log.error("[{}] Failed to flush {} events, re-queued", tableName(), batch.size(), e);
        }
    }

    protected abstract void persist(List<T> batch);

    protected abstract String tableName();
}
