# Architecture

## Services

Two Spring Boot services:

- **film-stream** — API for clients. Proxies TMDB, searches torrents via Jackett, streams HLS content from MinIO.
- **film-downloader** — Downloads torrents, converts to HLS, uploads to MinIO.

## Communication

Services communicate via **Redis Streams**:

- `download:stream` — film-stream publishes download tasks, film-downloader consumes them
- Payload field: `download-payload` (JSON-serialized torrent info)

## Data Flow

```
Client → film-stream → TMDB API        (movie search/metadata)
Client → film-stream → Jackett API     (torrent search)
Client → film-stream → Redis Stream    (send download task)
                        ↓
                    film-downloader     (download + HLS convert)
                        ↓
                      MinIO             (store HLS files)
                        ↓
Client → film-stream → MinIO           (stream HLS content)
```

## Storage

- **PostgreSQL** — application data, schema `film_stream`, managed by Flyway migrations
- **Redis** — torrent cache (TTL-based) + message broker (Streams)
- **MinIO** — HLS media files, bucket `content`

## HLS Streaming

film-stream proxies HLS content from MinIO. See `docs/streaming.md`.

## Persistence Layer

- **MyBatis** with XML mappers (`resources/mapper/*.xml`)
- Underscore-to-camelCase mapping enabled
