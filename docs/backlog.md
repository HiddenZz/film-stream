# Backlog

## Must Do

- **Download progress tracking** — Expose two endpoints for the client to track download/formatting progress published by film-downloader to Redis.
  - Redis structure (written by film-downloader): hash `progress:{DOWNLOADING|FORMATTING}:{tmdbId}`, field `{contentUuid}:{quality}`, value `0–100`. TTL 24h.
  - `GET /download/progress/{tmdbId}` — polling: read both hashes via `HGETALL`, return current snapshot.
  - `GET /download/progress/{tmdbId}/stream` — SSE: poll Redis on interval, push updates to client via `SseEmitter` until progress reaches 100 or connection drops.
  - Note: remove the dead `getDownloadProgress()` stub in `TorrentController` (missing `@GetMapping`).

- **HLS streaming proxy** — Proxy HLS content from MinIO through film-stream so MinIO is not exposed to clients.

  **MinIO layout** (bucket from `storage.s3`):
  ```
  {topPrefix}/{tmdbId}/{uuid}/master.m3u8
  {topPrefix}/{tmdbId}/{uuid}/{quality}/playlist.m3u8
  {topPrefix}/{tmdbId}/{uuid}/{quality}/segment_{N}.ts
  ```
  `content_ready.minio_path` stores the full path to `master.m3u8`. Relative paths in playlists: master references `{quality}/playlist.m3u8`, playlist references `segment_{N}.ts`.

  **Endpoints** — structure mirrors MinIO so relative paths resolve without rewriting:
  ```
  GET /stream/{contentUuid}/master.m3u8                → serves master playlist
  GET /stream/{contentUuid}/{quality}/playlist.m3u8     → serves quality playlist
  GET /stream/{contentUuid}/{quality}/{segment}.ts      → streams .ts segment
  ```

  **Implementation:**
  - `StreamController` + `StreamService`.
  - Resolve `contentUuid` → `minioPath` from `content_ready` table. For master — use `minioPath` as-is. For other files — replace `/master.m3u8` with `/{quality}/playlist.m3u8` or `/{quality}/{segment}.ts`.
  - Stream via `StreamingResponseBody` or `InputStreamResource` (don't buffer in memory).
  - Content-Type: `application/vnd.apple.mpegurl` for `.m3u8`, `video/mp2t` for `.ts`.
  - Sanitize `quality`/`segment` params — reject `..` and `/` (path traversal).
  - `Cache-Control`: immutable + long max-age for `.ts`, no-cache for `.m3u8`.
  - Return 404 if MinIO object not found (`ErrorResponseException`).

## Nice to Have

- **Cache TMDB movie details** — Cache responses from `GET /tmdb/movie/{id}` in Redis with TTL to reduce TMDB API calls. Approach: similar to `RedisSeedsService` — key pattern `tmdb:movie:{id}`, JSON value, configurable TTL.

## Ideas to Explore

- **Real-time torrent streaming** — Stream movie directly from torrent without waiting for full download + HLS conversion. Research: sequential piece downloading, on-the-fly transcoding, WebRTC or chunked HTTP delivery. Major architectural change in film-downloader.
