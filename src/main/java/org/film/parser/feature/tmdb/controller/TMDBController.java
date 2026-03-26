package org.film.parser.feature.tmdb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.tmdb.data.TMDBSearchResponse;
import org.film.parser.feature.tmdb.service.TMDBService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TMDB", description = "TMDB movie search and details")
@RestController
@RequestMapping("/tmdb")
@AllArgsConstructor
public class TMDBController {

    private final TMDBService tmdbService;

    @Operation(summary = "Search movies on TMDB")
    @GetMapping("/search")
    public ResponseEntity<TMDBSearchResponse> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "ru-RU") String language,
            @RequestParam(defaultValue = "1") int page) {

        return ResponseEntity.ok(tmdbService.searchMovies(query, language, page));
    }

    @Operation(summary = "Get movie details from TMDB")
    @GetMapping("/movie/{id}")
    public ResponseEntity<TMDBMovieDetails> movieDetails(
            @PathVariable long id,
            @RequestParam(defaultValue = "ru-RU") String language) {

        return ResponseEntity.ok(tmdbService.movieDetails(id, language));
    }
}