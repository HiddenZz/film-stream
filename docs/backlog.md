# Backlog

## Must Do

- **Download progress tracking** — Expose two endpoints for the client to track download/formatting progress published by film-downloader to Redis.
  - Redis structure (written by film-downloader): hash `progress:{DOWNLOADING|FORMATTING}:{tmdbId}`, field `{contentUuid}:{quality}`, value `0–100`. TTL 24h.
  - `GET /download/progress/{tmdbId}` — polling: read both hashes via `HGETALL`, return current snapshot.
  - `GET /download/progress/{tmdbId}/stream` — SSE: poll Redis on interval, push updates to client via `SseEmitter` until progress reaches 100 or connection drops.
  - Note: remove the dead `getDownloadProgress()` stub in `TorrentController` (missing `@GetMapping`).

- ~~**Movie readiness: completed event consumer**~~ — When film-downloader finishes processing, it should publish a completion event to a Redis Stream (e.g. `result:stream`). film-stream must consume it and persist readiness to PostgreSQL.
  - film-downloader side: publish to `result:stream`, field `data`, value `{tmdbId, contentUuid, minioPath}` on completion. `minioPath` format: `content/{tmdbId}/{contentUuid}/master.m3u8` (master playlist — contains all quality variants).
  - film-stream side: Redis Stream consumer (similar to how film-downloader consumes `download:stream`). On receive — save to new table `content_ready(tmdb_id, content_uuid, minio_path, created_at)` via Flyway migration.
  - API: `GET /movie/{tmdbId}/status` — check DB, return `{status: ready|processing|not_found}` + minioPath if ready.

- **Remove legacy provider parsers** — Lumex, Veoveo and all HLS playlist parsing code are no longer needed. Delete: parser services, resolver, related RestClient beans (`lumexRestClient`), playlist controller, content extractor. Clean up unused dependencies.

## Nice to Have

- **Cache TMDB movie details** — Cache responses from `GET /tmdb/movie/{id}` in Redis with TTL to reduce TMDB API calls. Approach: similar to `RedisSeedsService` — key pattern `tmdb:movie:{id}`, JSON value, configurable TTL.

## Ideas to Explore

- **Real-time torrent streaming** — Stream movie directly from torrent without waiting for full download + HLS conversion. Research: sequential piece downloading, on-the-fly transcoding, WebRTC or chunked HTTP delivery. Major architectural change in film-downloader.
