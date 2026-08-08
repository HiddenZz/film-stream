package org.film.parser.feature.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.film.parser.feature.movie.data.MovieLibraryResponse;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.data.MovieSummary;
import org.film.parser.feature.movie.service.MovieService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movies", description = "Movie library and metadata")
@RestController
@RequestMapping("/movie")
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get movie status by TMDB ID")
    @GetMapping("/{tmdbId}/status")
    public ResponseEntity<MovieStatusResponse> getStatus(@PathVariable long tmdbId) {
        return ResponseEntity.ok(movieService.getStatus(tmdbId));
    }

    @Operation(summary = "Get paginated movie library")
    @GetMapping
    public ResponseEntity<MovieLibraryResponse> getLibrary(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(movieService.getLibrary(offset, limit));
    }

    @Operation(summary = "Get a downloaded movie by TMDB ID")
    @GetMapping("/{tmdbId}")
    public ResponseEntity<MovieSummary> getMovie(@PathVariable long tmdbId) {
        return ResponseEntity.ok(movieService.getMovie(tmdbId));
    }

    @Operation(summary = "Get movie poster image")
    @GetMapping("/{tmdbId}/poster")
    public ResponseEntity<InputStreamResource> getPoster(@PathVariable long tmdbId) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(movieService.getPoster(tmdbId)));
    }
}
