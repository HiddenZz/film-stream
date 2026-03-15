# Backlog

## Must Do

- **Download progress tracking** — Expose two endpoints for the client to track download/formatting progress published by film-downloader to Redis.
  - Redis structure (written by film-downloader): hash `progress:{DOWNLOADING|FORMATTING}:{tmdbId}`, field `{contentUuid}:{quality}`, value `0–100`. TTL 24h.
  - `GET /download/progress/{tmdbId}` — polling: read both hashes via `HGETALL`, return current snapshot.
  - `GET /download/progress/{tmdbId}/stream` — SSE: poll Redis on interval, push updates to client via `SseEmitter` until progress reaches 100 or connection drops.
  - Note: remove the dead `getDownloadProgress()` stub in `TorrentController` (missing `@GetMapping`).

## Nice to Have

- **Cache TMDB movie details** — Cache responses from `GET /tmdb/movie/{id}` in Redis with TTL to reduce TMDB API calls. Approach: similar to `RedisSeedsService` — key pattern `tmdb:movie:{id}`, JSON value, configurable TTL.

## Ideas to Explore

- **Real-time torrent streaming** — Stream movie directly from torrent without waiting for full download + HLS conversion. Research: sequential piece downloading, on-the-fly transcoding, WebRTC or chunked HTTP delivery. Major architectural change in film-downloader.
