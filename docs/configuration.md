# Configuration

## Application Properties (`application.yml`)

| Service        | Config prefix         | Default                          |
|----------------|-----------------------|----------------------------------|
| Server         | `server.port`         | 8084                             |
| PostgreSQL     | `spring.datasource`   | localhost:5432/movie             |
| Redis          | `storage.redis`       | localhost:6379                   |
| MinIO          | `storage.minio`       | http://127.0.0.1:9000           |
| Jackett        | `torrent.jackett`     | http://localhost:9117/api/v2.0/ |
| TMDB           | `tmdb`                | https://api.themoviedb.org/3    |

## Configuration Properties Classes

- `MinioProperties` — MinIO credentials/bucket (`storage.minio`)
- `RedisProperties` — Redis connection and TTL config (`storage.redis`)
- `JackettProperties` — Jackett API URL and key (`torrent.jackett`)
- `TMDBProperties` — TMDB API base URL and Bearer token (`tmdb`)
- `RestTemplateConfigurationProperties` — API endpoints (`network.rest-template`)

All scanned via `@ConfigurationPropertiesScan` in `Main.java`.

## RestClient Beans

| Bean                | Location                     | Purpose                          |
|---------------------|------------------------------|----------------------------------|
| `restClient`        | `RestClientConfiguration`    | Default API client               |
| `lumexRestClient`   | `RestClientConfiguration`    | Lumex provider (to be removed)   |
| `proxyRestClient`   | `RestClientConfiguration`    | Debug client (proxy on :8080)    |
| `jackettClient`     | `TorrentConfiguration`       | Jackett API (custom Jackson)     |
| `tmdbClient`        | `TMDBConfiguration`          | TMDB API (Bearer auth, snake_case) |

## Database

- Schema: `film_stream`
- Migrations: Flyway (auto-applied on startup)
- ORM: MyBatis with XML mappers in `resources/mapper/*.xml`
