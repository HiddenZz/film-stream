package org.film.parser.feature.torrent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.catalina.connector.Response;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.JackettResults;
import org.film.parser.feature.torrent.data.Seed;
import org.film.parser.feature.torrent.data.TorrentDownloadRequestDto;
import org.film.parser.feature.torrent.service.TorrentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Torrents", description = "Torrent search and download")
@RestController
@AllArgsConstructor
public class TorrentController {

    private final TorrentService torrentService;

    @Operation(summary = "Search torrent seeds for a movie")
    @GetMapping("/seeds/")
    public ResponseEntity<List<Seed>> searchSeed(@RequestParam long movieId) {
        final List<Seed> seeds = torrentService.searchSeeds(movieId);

        return ResponseEntity.ok(seeds);
    }

    @Operation(summary = "Get torrent details by movie and GUID")
    @GetMapping("/torrent/")
    public ResponseEntity<JackettResult> getTorrentInfo(@RequestParam long movieId, @RequestParam String guid) {
        return ResponseEntity.ok(torrentService.getTorrent(movieId, guid));
    }

    @Operation(summary = "Request torrent download by movie and GUID")
    @PostMapping("/torrent/download/")
    public ResponseEntity<Object> sendToDownload(@RequestBody TorrentDownloadRequestDto requestDto) {
        return ResponseEntity.ok(torrentService.requestDownload(requestDto.tmdbId(), requestDto.guid()));
    }

    public ResponseEntity<Object> getDownloadProgress(@RequestParam long queueId) {
        return ResponseEntity.ok("");
    }
}
