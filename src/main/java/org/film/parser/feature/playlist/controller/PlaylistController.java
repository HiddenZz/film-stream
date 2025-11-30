package org.film.parser.feature.playlist.controller;

import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/{contentId}/index.m3u8")
    public ResponseEntity<Resource> getMasterPlaylist(@PathVariable long contentId) {
        final Playlist playlist = playlistService.getMasterPlaylist(contentId, "movie");

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(playlist.content());
    }

    @GetMapping("/content/{contentId}/**")
    public ResponseEntity<Resource> getMediaContent(
            @PathVariable long contentId,
            HttpServletRequest request
    ) {
        final String fullPath = request.getServletPath();

        if (fullPath.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        final Playlist playlist = playlistService.getMediaPlaylistByPath(fullPath, contentId);

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(playlist.content());
    }

    @GetMapping("/movie/{contentId}/segment/**/**/seg-{index}.ts")
    public ResponseEntity<Resource> getMovie(
            @PathVariable long contentId,
            @PathVariable int index,
            HttpServletRequest request
    ) {
        final String fullPath = request.getServletPath();

        if (fullPath.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        final Playlist playlist = playlistService.getMediaPlaylistByPath(fullPath, contentId);

        if (playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(playlist.content());
    }

}
