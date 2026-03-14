package org.film.parser.feature.movie.controller;

import lombok.AllArgsConstructor;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{tmdbId}/status")
    public ResponseEntity<MovieStatusResponse> getStatus(@PathVariable long tmdbId) {
        return ResponseEntity.ok(movieService.getStatus(tmdbId));
    }
}