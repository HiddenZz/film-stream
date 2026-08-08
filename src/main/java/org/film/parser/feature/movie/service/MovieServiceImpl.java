package org.film.parser.feature.movie.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.AppProperties;
import org.film.parser.core.configuration.properties.TMDBProperties;
import org.film.parser.core.exception.ResourceNotFoundException;
import org.film.parser.feature.movie.data.ContentReady;
import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.data.ContentVersion;
import org.film.parser.feature.movie.data.Movie;
import org.film.parser.feature.movie.data.MovieLibraryResponse;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.data.MovieSummary;
import org.film.parser.feature.movie.repository.ContentReadyRepository;
import org.film.parser.feature.movie.repository.MovieRepository;
import org.film.parser.feature.movie.repository.ProgressRepository;
import org.film.parser.feature.stream.client.ContentStorageClient;
import org.film.parser.feature.tmdb.client.TMDBClient;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private static final String POSTER_KEY_FORMAT = "posters/%d.jpg";
    private static final String POSTER_ENDPOINT_FORMAT = "/movie/%d/poster";

    private final ContentReadyRepository repository;
    private final MovieRepository movieRepository;
    private final ProgressRepository progressRepository;
    private final TMDBClient tmdbClient;
    private final ContentStorageClient contentStorageClient;
    private final RestClient restClient;
    private final TMDBProperties tmdbProperties;
    private final AppProperties appProperties;

    @Override
    public void saveReady(ContentReadyEvent event) {
        final ContentReady entity = ContentReady.builder()
                .tmdbId(event.tmdbId())
                .contentUuid(event.contentUuid())
                .minioPath(event.minioPath())
                .build();
        repository.insert(entity);
        log.info("Movie ready saved: tmdbId={}, contentUuid={}", event.tmdbId(), event.contentUuid());

        try {
            saveMovieMetadata(event.tmdbId());
        } catch (Exception e) {
            log.warn("Failed to save movie metadata for tmdbId={}: {}", event.tmdbId(), e.getMessage());
        }
    }

    private void saveMovieMetadata(long tmdbId) {
        if (movieRepository.findByTmdbId(tmdbId) != null) {
            return;
        }

        TMDBMovieDetails details = tmdbClient.movieDetails(tmdbId, "ru-RU");

        String posterPath = null;
        if (details.getPosterPath() != null) {
            posterPath = downloadPoster(tmdbId, details.getPosterPath());
        }

        Movie movie = Movie.builder()
                .tmdbId(tmdbId)
                .title(details.getTitle())
                .originalTitle(details.getOriginalTitle())
                .overview(details.getOverview())
                .posterPath(posterPath)
                .releaseDate(details.getReleaseDate())
                .voteAverage(details.getVoteAverage())
                .runtime(details.getRuntime())
                .build();

        movieRepository.insert(movie);
        log.info("Movie metadata saved: tmdbId={}, title={}", tmdbId, details.getTitle());
    }

    private String downloadPoster(long tmdbId, String tmdbPosterPath) {
        try {
            String imageUrl = tmdbProperties.imageBaseUrl() + tmdbPosterPath;
            byte[] imageBytes = restClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .body(byte[].class);

            if (imageBytes == null || imageBytes.length == 0) {
                log.warn("Empty poster response for tmdbId={}", tmdbId);
                return null;
            }

            String objectKey = POSTER_KEY_FORMAT.formatted(tmdbId);
            contentStorageClient.putObject(objectKey, new ByteArrayInputStream(imageBytes), imageBytes.length, "image/jpeg");
            return POSTER_ENDPOINT_FORMAT.formatted(tmdbId);
        } catch (Exception e) {
            log.warn("Failed to download poster for tmdbId={}: {}", tmdbId, e.getMessage());
            return null;
        }
    }

    @Override
    public MovieStatusResponse getStatus(long tmdbId) {
        final List<ContentReady> versions = repository.findByTmdbId(tmdbId);
        if (!versions.isEmpty()) {
            final List<ContentVersion> contentVersions = versions.stream()
                    .map(v -> new ContentVersion(v.getContentUuid(), v.getMinioPath()))
                    .toList();
            return MovieStatusResponse.ready(contentVersions);
        }

        return progressRepository.isProcessing(tmdbId) ? MovieStatusResponse.processing() : MovieStatusResponse.notFound();
    }

    @Override
    public MovieLibraryResponse getLibrary(int offset, int limit) {
        int clampedLimit = Math.min(Math.max(limit, 1), 100);
        int clampedOffset = Math.max(offset, 0);

        List<Movie> movies = movieRepository.findAll(clampedLimit, clampedOffset);
        long total = movieRepository.count();

        List<MovieSummary> summaries = movies.stream()
                .map(this::toSummary)
                .toList();

        return new MovieLibraryResponse(summaries, total, clampedOffset, clampedLimit);
    }

    @Override
    public MovieSummary getMovie(long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found for tmdbId: " + tmdbId);
        }
        return toSummary(movie);
    }

    @Override
    public InputStream getPoster(long tmdbId) {
        return contentStorageClient.getObject(POSTER_KEY_FORMAT.formatted(tmdbId));
    }

    private MovieSummary toSummary(Movie movie) {
        String posterUrl = movie.getPosterPath() != null
                ? appProperties.publicBaseUrl() + movie.getPosterPath()
                : null;
        return MovieSummary.builder()
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterUrl(posterUrl)
                .releaseDate(movie.getReleaseDate())
                .voteAverage(movie.getVoteAverage())
                .runtime(movie.getRuntime())
                .build();
    }

}
