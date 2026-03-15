# HLS Streaming

film-stream proxies HLS content from MinIO so MinIO is not exposed to clients.

## MinIO Layout

Bucket: `storage.minio.buckets.top-prefix` (default: `content`).

```
{tmdbId}/{uuid}/master.m3u8
{tmdbId}/{uuid}/{quality}/playlist.m3u8
{tmdbId}/{uuid}/{quality}/segment_{N}.ts
```

`content_ready.minio_path` stores the object key of `master.m3u8` for a given `contentUuid`. All related paths are derived from it by replacing `/master.m3u8` with the target suffix.

## API

```
GET /stream/{contentUuid}/master.m3u8
GET /stream/{contentUuid}/{quality}/playlist.m3u8
GET /stream/{contentUuid}/{quality}/{segment}
```

Endpoint structure mirrors MinIO layout — relative paths inside `.m3u8` playlists resolve correctly without rewriting.

## Implementation

- `ContentStorageClient` / `MinioContentStorageClient` — reads objects from MinIO.
- `StreamService` / `StreamServiceImpl` — resolves `contentUuid` → `minioPath` via `content_ready`, builds object keys, delegates to `ContentStorageClient`.
- `StreamController` — streams via `StreamingResponseBody` (no in-memory buffering).

## Caching

- `.m3u8` playlists: `Cache-Control: no-cache`
- `.ts` segments: `Cache-Control: public, max-age=31536000, immutable`

## Security

`quality` and `segment` path variables are validated to reject `..` and `/` (path traversal protection).