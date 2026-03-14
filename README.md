# film-stream

Backend service for a self-hosted movie watching platform. Streams downloaded movies, proxies TMDB API requests for movie metadata, and coordinates torrent search/download via event-driven communication with [film-downloader](link-to-repo).

## How it works

1. User searches for a movie — film-stream proxies the request to TMDB and returns metadata
2. User picks a torrent — film-stream searches Jackett for available torrents and sends a download task to film-downloader via Redis Streams
3. film-downloader downloads the torrent, converts to HLS, and stores in MinIO
4. User watches the movie — film-stream serves HLS content from MinIO

**Future goal:** real-time streaming from torrent without waiting for full download and HLS conversion.

## Architecture

Two services communicating via Redis Streams:

- **film-stream** (this repo) — API gateway, TMDB proxy, torrent search, HLS streaming
- **film-downloader** — torrent download, HLS conversion, MinIO upload

Both services are Spring Boot / Java applications.

## Infrastructure

- **PostgreSQL** — persistence
- **Redis** — cache + message broker (Redis Streams)
- **MinIO** — object storage for media files
- **Jackett** — torrent search aggregator

## Build & Run

```bash
./gradlew build      # build
./gradlew bootRun    # run
./gradlew test       # tests
```
