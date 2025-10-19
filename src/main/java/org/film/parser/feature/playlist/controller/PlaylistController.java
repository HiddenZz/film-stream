package org.film.parser.feature.playlist.controller;

import org.film.parser.feature.parser.playlist.service.PlaylistParserService;
import org.film.parser.feature.parser.playlist.service.PlaylistParserServiceImpl;
import org.film.parser.feature.playlist.data.Playlist;
import org.film.parser.feature.playlist.service.PlaylistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistParserService servivce;

    PlaylistController(PlaylistService playlistService, PlaylistParserService service) {
        this.playlistService = playlistService;
        this.servivce = service;
    }


    @GetMapping("/")
    public ResponseEntity<String> getPlayer() {

        final var playlist = servivce.parseMasterPlaylist(5003510);

        if(playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok("good");
    }

    @GetMapping("/{movieId}/playlist.m3u8")
    public ResponseEntity<Playlist> getPlaylist(String id, String type) {

        final Playlist playlist = playlistService.getPlaylist(id, type);

        if(playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(playlist);
    }

}
