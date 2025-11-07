# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for parsing and streaming video content. The application fetches HLS playlists from external video streaming providers (like Lumex), processes them, and stores content in MinIO object storage. It acts as a middleware service that handles video content parsing and transformation.

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

The application uses a resolver pattern for handling different video providers:

- `MasterPlaylistParserService` interface defines the contract for all parsers
- Each provider implementation (e.g., Lumex) registers itself with a unique name via `getName()`
- `MasterPlaylistParserResolver` maintains a map of parser name → implementation
- At runtime, the resolver selects the correct parser based on the `AvailablePlayer.name()`

**Key Files:**
- `MasterPlaylistParserResolver.java:17` - Parser resolution logic
- `PlaylistParserServiceImpl.java:32-36` - Parser selection flow

### Provider Implementation: Lumex

The Lumex parser implements a complex multi-step extraction process:

1. **Iframe Parsing** (`parseSegmentInfo`) - Extracts clientId, contentType, contentId from iframe URL
2. **Content API Call** (`extractFirstMedia`) - Fetches media info from Lumex API
3. **Playlist URL Parsing** (`parseUrlMasterPlaylist`) - POSTs to get actual playlist URL, with retry logic (3 attempts)
4. **Download** (`downloadMasterPlaylist`) - Fetches the master playlist bytes
5. Returns `MasterMedia` object containing parsed data

**Important:** The Lumex parser uses a dedicated `lumexRestClient` configured with specific headers (Origin, Referer, User-Agent) required by the Lumex API.

### RestClient Configuration

Three separate RestClient beans are configured:

- `restClient` - Default client for the main parsing host API
- `lumexRestClient` - Lumex-specific with cookie handling and custom headers
- `proxyRestClient` - Configured with SSL trust-all and HTTP proxy (127.0.0.1:8080) for debugging

**Location:** `RestTemplateConfiguration.java`

### Error Handling Strategy

The codebase uses custom exceptions for different failure scenarios:

- `ParseIframeException` - Iframe URL parsing failures
- `ParseMasterPlaylistException` - General master playlist parsing errors
- `PlaylistDownloadException` - Playlist download failures
- `ContentExtractException` / `ContentRemoteExtractException` - Content extraction errors
- `ContentParseException` - Content parsing errors

These exceptions are thrown at specific layers and caught/wrapped at higher levels.

### Data Models

- `AvailablePlayer` - Represents a video player option from external API
- `MasterMedia` - Contains parsed master playlist data (name, bytes, URL)
- `ContentPlaylistMedia` - Represents individual media content in playlists
- `LumexResponse` / `LumexContentPlayer` - Lumex API response structures

## Configuration

### Application Properties

Configuration is loaded from `application.yml`:

- **Server:** Runs on port 8084
- **Database:** PostgreSQL at localhost:5432 (database: movie, user: movie_user)
- **MinIO:** Endpoint at http://127.0.0.1:9000/ (bucket: film)
- **External APIs:**
  - Parse host: https://api4.rhhhhhhh.live/
  - Lumex host: https://api.lumex.space

### Configuration Classes

Spring `@ConfigurationProperties` pattern is used:

- `MinioProperties` - MinIO credentials and bucket configuration
- `RestTemplateConfigurationProperties` - External API endpoints
- `LumexConfig` - Lumex-specific configuration (name and host)

All properties classes are scanned via `@ConfigurationPropertiesScan` in `Main.java:11`

## Technology Stack

- **Spring Boot 4.0.0-M3** (Milestone release)
- **PostgreSQL** with MyBatis for persistence
- **MinIO** for object storage
- **Flyway** for database migrations (currently disabled)
- **RxJava 3** for reactive operations
- **M3U8 Parser** (io.lindstrom) for HLS playlist parsing
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction
- **Testcontainers** for integration tests

## Testing Setup

Tests use Testcontainers for PostgreSQL:

- `AbstractConfigurationTest` - Base test class for integration tests
- `TestJdbcConfig` - JDBC configuration for tests

Run tests with embedded PostgreSQL via Testcontainers - no manual database setup required.

## Important Implementation Notes

### Current State

Several service methods return `null` or empty implementations, indicating the application is in active development:

- `PlaylistServiceIml.getPlaylist()` returns null
- `PlaylistParserServiceImpl.parseMasterPlaylist()` returns empty string
- `MinioClientImpl.fileExists()` returns false

### RestClient Usage

The codebase has migrated from `RestTemplate` to Spring's `RestClient` API. When making HTTP calls, use the appropriate client bean based on the target:

- Use `restClient` for the main parsing API
- Use `lumexRestClient` for Lumex API calls (maintains cookies and proper headers)
- Use `proxyRestClient` for debugging with proxy

### Retry Logic

HTTP requests to Lumex use retry logic (`requestWithRetry` in `LumexMasterPlaylistParserService.java:112`). This method retries up to N times on failure or error status codes.
