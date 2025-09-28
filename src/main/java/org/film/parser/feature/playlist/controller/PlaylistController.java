package org.film.parser.feature.playlist.controller;

import org.film.parser.feature.playlist.data.Playlist;
import org.film.parser.feature.playlist.service.PlaylistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaylistController {

    private final PlaylistService playlistService;

    PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Playlist> getPlaylist(String id, String type) {

        final Playlist playlist = playlistService.getPlaylist(id, type);

        if(playlist == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(playlist);
    }

}
