package org.film.parser.feature.playlist.controller;

import org.film.parser.feature.parser.playlist.service.PlaylistParserService;
import org.film.parser.feature.playlist.data.Playlist;
import org.film.parser.feature.playlist.service.PlaylistService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistParserService playlistParserService;

    PlaylistController(PlaylistService playlistService, PlaylistParserService playlistParserService) {
        this.playlistService = playlistService;
        this.playlistParserService = playlistParserService;
    }

    @GetMapping("/")
    public ResponseEntity<String> getPlayer(@RequestParam() long id) {

        final var playlist = playlistParserService.parseMasterPlaylist(id);

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok("good");
    }

    @GetMapping("/{movieId}/index.m3u8")
    public ResponseEntity<Resource> getPlaylist(@PathVariable() long movieId) {

        final Playlist playlist = playlistService.getMasterPlaylist(movieId, "movie");

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(playlist.content());
    }

    @GetMapping("/{movieId}/{quality}/index.m3u8")
    public ResponseEntity<Resource> getPlaylist(@PathVariable() long movieId,
                                                @PathVariable() int quality) {
        final Playlist playlist = playlistService.getMediaPlaylist(movieId, quality);

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(playlist.content());
    }

}
