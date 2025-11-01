package org.film.parser.feature.playlist.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class EphemeralCache<Key, Value> {

    private final Map<Key, CompletableFuture<CacheEntry<Value>>> cache = new ConcurrentHashMap<>();
    private final Executor saveExecutor;
    private final String cacheName;

    public EphemeralCache(Executor saveExecutor, String cacheName) {
        this.saveExecutor = saveExecutor;
        this.cacheName = cacheName;
    }

    public CompletableFuture<CacheEntry<Value>> getOrCompute(
            Key key,
            Function<Key, Value> valueLoader,
            Consumer<Value> valueSaver
    ) {
        return cache.computeIfAbsent(key, k -> {
            log.debug("[{}] Cache miss for key: {}. Computing value...", cacheName, key);

            return CompletableFuture.supplyAsync(() -> {
                Value value = valueLoader.apply(k);

                CompletableFuture<Void> saveFuture = saveAsync(k, value, valueSaver);

                CacheEntry<Value> entry = new CacheEntry<>(value, saveFuture);

                log.info("[{}] Value computed and cached for key: {}. Save status: {}",
                        cacheName, key, entry.isSaving() ? "IN_PROGRESS" : "COMPLETED");

                return entry;
            });
        });
    }

    private CompletableFuture<Void> saveAsync(Key key, Value value, Consumer<Value> valueSaver) {
        return CompletableFuture.runAsync(() -> {
                                    try {
                                        log.debug("[{}] Saving value for key: {}", cacheName, key);
                                        valueSaver.accept(value);
                                        log.info("[{}] Successfully saved value for key: {}", cacheName, key);
                                    } catch (Exception e) {
                                        log.error("[{}] Failed to save value for key: {}. Error: {}",
                                                cacheName, key, e.getMessage(), e);
                                        throw new RuntimeException("Save failed for key: " + key, e);
                                    }
                                }, saveExecutor)
                                .whenCompleteAsync((result, ex) -> {
                                    // Ephemeral eviction: remove from cache after save completes
                                    cache.remove(key);

                                    if (ex == null) {
                                        log.debug("[{}] Save completed successfully for key: {}. Evicted from cache.", cacheName, key);
                                    } else {
                                        log.warn("[{}] Save failed for key: {}. Evicted from cache to prevent stale data.", cacheName, key);
                                    }
                                });
    }

    public void invalidate(Key key) {
        cache.remove(key);
        log.debug("[{}] Manually invalidated cache for key: {}", cacheName, key);
    }

    public Map<String, Object> getStats() {
        return Map.of(
                "cacheName", cacheName,
                "cacheSize", cache.size()
        );
    }


    public record CacheEntry<V>(V value, CompletableFuture<Void> saveFuture) {

        public boolean isSaved() {
            return saveFuture.isDone() && !saveFuture.isCompletedExceptionally();
        }

        public boolean isSaving() {
            return !saveFuture.isDone();
        }

        public boolean hasSaveFailed() {
            return saveFuture.isCompletedExceptionally();
        }
    }
}