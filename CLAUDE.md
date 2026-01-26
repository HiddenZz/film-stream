# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot application for video content management:
- Parses HLS playlists from video providers (Lumex, Veoveo)
- Searches torrent content via Jackett API
- Stores processed playlists in MinIO object storage
- Integrates with TMDB for movie metadata

## Build & Run Commands

### Build
```bash
./gradlew build
```

### Run Application
```bash
./gradlew bootRun
```

### Run Tests
```bash
./gradlew test
```

### Run Specific Test
```bash
./gradlew test --tests "ClassName.testMethodName"
```

## Architecture Overview

### Core Processing Flow

The application follows a multi-layered parsing pipeline:

1. **Controller Layer** (`PlaylistController`) - Receives HTTP requests with content IDs
2. **Service Orchestration** (`PlaylistParserServiceImpl`) - Coordinates the parsing workflow
3. **External API Client** (`PlaylistRhhParserClientImpl`) - Fetches available players from external API
4. **Parser Resolution** (`MasterPlaylistParserResolver`) - Dynamically resolves the appropriate parser based on player name
5. **Provider-Specific Parsers** (e.g., `LumexMasterPlaylistParserService`) - Handles provider-specific parsing logic
6. **Content Extraction** (`ContentRemoteExtractorService`) - Downloads remote content
7. **Storage** (`MinioClientImpl`) - Stores processed playlists in MinIO

### Parser Strategy Pattern

Resolver pattern for video provider handling:
- `MasterPlaylistParserService` - Contract for all parsers
- Each provider (Lumex, Veoveo) registers via `getName()`
- `MasterPlaylistParserResolver` maps parser name → implementation
- Runtime parser selection based on `AvailablePlayer.name()`

**Key Files:**
- `MasterPlaylistParserResolver.java:17` - Resolution logic
- `PlaylistParserServiceImpl.java:32-36` - Parser selection

### Torrent Feature

Jackett integration for torrent search:

**Flow:**
1. `TorrentController.searchSeed()` - Receives TMDB movie ID
2. `TorrentServiceImpl.searchSeeds()` - Orchestrates search
3. `TorrentQueryBuilder` - Builds query: `"{title} {year} год"`
4. `JackettClient.search()` - Calls Jackett API at `/indexers/all/results/`
5. `JackettResultToSeedMapper` - Maps results to `Seed` objects

**Configuration (`TorrentConfiguration.java`):**
- Custom `ObjectMapper` with `UPPER_CAMEL_CASE` naming strategy for Jackett API
- `FAIL_ON_UNKNOWN_PROPERTIES = false` to ignore extra JSON fields (like `Indexers`)
- Dedicated `RestClient` bean for Jackett communication

**Data Models:**
- `JackettResults` - Root response containing `List<JackettResult>`
- `JackettResult` - Individual torrent (tracker, title, guid, link, seeders, peers, size, gain)
- `Seed` - Simplified model (guid, title, externalLink)
- `TMBDMovieInfo` - Movie metadata (title, year)

**Endpoints:**
- `GET /seeds/?movieId={tmdbId}` - Search torrents by TMDB ID

**Important:** All Jackett data models require `@NoArgsConstructor` for Jackson deserialization.

### Redis Cache & Streams

Redis integration via Spring Data Redis for torrent caching:

**Flow:**
1. `RedisSeedsService.sendTorrents()` - Caches torrent results and publishes to stream
2. Uses pipelined execution for batch operations
3. Generates SHA256 hash as cache GUID from Jackett GUID
4. Stores JSON serialized `JackettResult` with TTL (configurable via `torrentCachePerSeconds`)
5. Publishes to Redis Stream `download:stream` with `download-payload` field

**Configuration (`RedisConfiguration.java`):**
- `LettuceConnectionFactory` for connection management
- `StringRedisTemplate` for string operations
- Connection to `storage.redis.url:port`

**Operations:**
- `SET seed:{guid} {json} EX {ttl}` - Cache torrent with expiration
- `XADD download:stream * download-payload {json}` - Publish to stream
- `GET seed:{guid}` - Retrieve cached torrent

**Key Pattern:** `seed:{sha256(jackettGuid)}`

### Provider Implementation: Lumex

The Lumex parser implements a complex multi-step extraction process:

1. **Iframe Parsing** (`parseSegmentInfo`) - Extracts clientId, contentType, contentId from iframe URL
2. **Content API Call** (`extractFirstMedia`) - Fetches media info from Lumex API
3. **Playlist URL Parsing** (`parseUrlMasterPlaylist`) - POSTs to get actual playlist URL, with retry logic (3 attempts)
4. **Download** (`downloadMasterPlaylist`) - Fetches the master playlist bytes
5. Returns `MasterMedia` object containing parsed data

**Important:** The Lumex parser uses a dedicated `lumexRestClient` configured with specific headers (Origin, Referer, User-Agent) required by the Lumex API.

### RestClient Configuration

Multiple RestClient beans in `RestClientConfiguration.java`:

- `restClient` - Default client for parsing host API
- `lumexRestClient` - Lumex-specific (cookie handling, custom headers)
- `proxyRestClient` - Debug client (SSL trust-all, HTTP proxy 127.0.0.1:8080)
- Jackett client configured in `TorrentConfiguration.java` with custom Jackson settings

### Custom Exceptions

- `ParseIframeException` - Iframe URL parsing failures
- `ParseMasterPlaylistException` - Master playlist parsing errors
- `PlaylistDownloadException` - Playlist download failures
- `ContentExtractException` / `ContentRemoteExtractException` - Content extraction errors
- `ContentParseException` - Content parsing errors
- `JackettException` - Jackett API failures

### Data Models

**Playlist Domain:**
- `AvailablePlayer` - Video player option from external API
- `MasterMedia` - Parsed master playlist (name, bytes, URL)
- `ContentPlaylistMedia` - Individual media content in playlists
- `LumexResponse` / `LumexContentPlayer` - Lumex API responses

**Torrent Domain:**
- `JackettResults` - Root Jackett response
- `JackettResult` - Single torrent result
- `Seed` - Simplified torrent representation
- `TMBDMovieInfo` - Movie metadata from TMDB

## Configuration

### Application Properties

Configuration in `application.yml`:

- **Server:** Port 8084
- **Database:** PostgreSQL localhost:5432, db=movie, schema=film_stream, user=movie_user
- **Redis:** localhost:6379 (`storage.redis`)
- **MinIO:** http://127.0.0.1:9000/, bucket=content, user=minioadmin
- **Jackett:** http://localhost:9117/api/v2.0/ (apiKey in config)
- **External APIs:**
  - Parse host: https://api4.rhserv.vu/
  - Lumex: https://api.lumex.space
- **Flyway:** Enabled, schema=film_stream
- **MyBatis:** Mappers in classpath:mapper/*.xml, underscore-to-camelCase mapping

### Configuration Classes

Spring `@ConfigurationProperties` classes:

- `MinioProperties` - MinIO credentials/bucket (`storage.minio`)
- `RedisProperties` - Redis connection and TTL config (`storage.redis`)
- `RestTemplateConfigurationProperties` - API endpoints (`network.rest-template`)
- `LumexConfig` - Lumex provider config (`parsers.movie.lumex`)
- `JackettProperties` - Jackett API config (`torrent.jackett`)

Properties scanned via `@ConfigurationPropertiesScan` in Main.java:11

## Technology Stack

- **Spring Boot 3.5.0**
- **PostgreSQL** + MyBatis for persistence
- **Redis** via Spring Data Redis (Lettuce) for caching and streams
- **MinIO** for object storage
- **Flyway** for database migrations
- **Jackett API** for torrent search
- **TMDB API** for movie metadata
- **RxJava 3** for reactive operations
- **M3U8 Parser** (io.lindstrom) for HLS parsing
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction
- **Testcontainers** for integration tests

## Testing Setup

Tests use Testcontainers for PostgreSQL:

- `AbstractConfigurationTest` - Base test class for integration tests
- `TestJdbcConfig` - JDBC configuration for tests

Run tests with embedded PostgreSQL via Testcontainers - no manual database setup required.

## Important Implementation Notes

### Jackson Deserialization

**Critical for Jackett integration:**
- All Jackett DTOs require `@NoArgsConstructor` for Jackson
- Use explicit `@JsonProperty` annotations for JSON field mapping
- `FAIL_ON_UNKNOWN_PROPERTIES = false` to ignore extra fields

### RestClient Selection

Use appropriate RestClient for each API:
- `restClient` - Main parsing API
- `lumexRestClient` - Lumex (requires specific headers/cookies)
- `proxyRestClient` - Debugging with proxy
- Jackett client - Configured in TorrentConfiguration with custom Jackson

### Redis Operations

Use `StringRedisTemplate` for Redis operations:
- `opsForValue().set(key, value, Duration)` - SET with TTL
- `opsForValue().get(key)` - GET operation
- `opsForStream().add(record)` - XADD to streams
- `executePipelined(callback)` - Batch operations

### Retry Logic

Lumex requests use `requestWithRetry()` (LumexMasterPlaylistParserService.java:112) - retries N times on failure.

### TODOs

- `TorrentServiceImpl.searchSeeds()` - Hardcoded movie info, needs TMDB integration via TMDBClient
- `TorrentController` - `sendToDownload()` and `getDownloadProgress()` not implemented
