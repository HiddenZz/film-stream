# Torrent Feature

## Search Flow

1. `TorrentController.searchSeed()` — receives TMDB movie ID
2. `TorrentServiceImpl.searchSeeds()` — gets movie info, builds query, calls Jackett
3. `TorrentQueryBuilder` — builds search query: `"{title} {year} год"`
4. `JackettClient.search()` — calls Jackett API at `/indexers/all/results/`
5. `JackettResultToSeedMapper` — maps results to `Seed` objects

## Jackett Integration

**Endpoint:** `GET /api/v2.0/indexers/all/results/?apikey={key}&Query={query}`

**Jackson config** (in `TorrentConfiguration.java`):
- `UPPER_CAMEL_CASE` property naming strategy
- `FAIL_ON_UNKNOWN_PROPERTIES = false`
- All DTOs require `@NoArgsConstructor`

**Models:**
- `JackettResults` → `List<JackettResult>`
- `JackettResult` — tracker, title, guid, link, seeders, peers, size, gain
- `Seed` — simplified model (guid, title, externalLink)

## Redis Cache & Streams

**Caching torrent results** (`RedisSeedsService`):
- Key pattern: `seed:{sha256(jackettGuid)}`
- Value: JSON-serialized `JackettResult`
- TTL: configurable via `torrentCachePerSeconds`
- Uses pipelined execution for batch SET operations

**Publishing download tasks:**
- Stream: `download:stream`
- Field: `download-payload`
- Command: `XADD download:stream * download-payload {json}`

## API Endpoints

- `GET /seeds/?movieId={tmdbId}` — search torrents by TMDB movie ID
- `POST /seeds/download` — send torrent to download (not implemented)
- `GET /seeds/download/progress` — get download progress (not implemented)
