# Backlog

## Must Do

- **TMDB integration** — `TorrentServiceImpl.searchSeeds()` has hardcoded movie info. Implement `TMDBClient` to fetch movie metadata (title, year) by TMDB ID. Approach: create a REST client for TMDB API, add proxy endpoint for client apps.

- **Download endpoints** — `TorrentController.sendToDownload()` and `getDownloadProgress()` are stubs. Implement: send selected torrent to Redis Stream, expose SSE or polling endpoint for download progress from film-downloader.

- **Remove legacy provider parsers** — Lumex, Veoveo and all HLS playlist parsing code are no longer needed. Delete: parser services, resolver, related RestClient beans (`lumexRestClient`), playlist controller, content extractor. Clean up unused dependencies.

## Nice to Have

(empty)

## Ideas to Explore

- **Real-time torrent streaming** — Stream movie directly from torrent without waiting for full download + HLS conversion. Research: sequential piece downloading, on-the-fly transcoding, WebRTC or chunked HTTP delivery. Major architectural change in film-downloader.
