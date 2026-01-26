package org.film.parser.feature.torrent.controller;

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

@RestController
@AllArgsConstructor
public class TorrentController {

    private final TorrentService torrentService;

    @GetMapping("/seeds/")
    public ResponseEntity<List<Seed>> searchSeed(@RequestParam long movieId) {

        final List<Seed> seeds = torrentService.searchSeeds(movieId);

        return ResponseEntity.ok(seeds);
    }

    @GetMapping("/torrent/")
    public ResponseEntity<JackettResult> getTorrentInfo(@RequestParam String guid) {

        final JackettResult result = torrentService.getTorrent(guid);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/torrent/download/")
    public ResponseEntity<Object> sendToDownload(@RequestBody TorrentDownloadRequestDto requestDto) {
        return ResponseEntity.ok(torrentService.requestDownload(requestDto.guid()));
    }

    public ResponseEntity<Object> getDownloadProgress(@RequestParam long queueId) {
        return ResponseEntity.ok("");
    }
}
