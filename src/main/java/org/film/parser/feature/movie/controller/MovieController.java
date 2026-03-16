package org.film.parser.feature.movie.controller;

import lombok.AllArgsConstructor;
import org.film.parser.feature.movie.data.MovieLibraryResponse;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movie")
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{tmdbId}/status")
    public ResponseEntity<MovieStatusResponse> getStatus(@PathVariable long tmdbId) {
        return ResponseEntity.ok(movieService.getStatus(tmdbId));
    }

    @GetMapping
    public ResponseEntity<MovieLibraryResponse> getLibrary(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(movieService.getLibrary(offset, limit));
    }
}
