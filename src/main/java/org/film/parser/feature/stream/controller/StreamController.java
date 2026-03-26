package org.film.parser.feature.stream.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.film.parser.feature.stream.service.StreamService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Tag(name = "Streaming", description = "HLS video streaming proxy")
@RestController
@RequestMapping("/stream")
@AllArgsConstructor
public class StreamController {

    private static final String M3U8_CONTENT_TYPE = "application/vnd.apple.mpegurl";
    private static final String TS_CONTENT_TYPE = "video/mp2t";

    private final StreamService streamService;

    @Operation(summary = "Get HLS master playlist")
    @GetMapping(value = "/{contentUuid}/master.m3u8", produces = M3U8_CONTENT_TYPE)
    public ResponseEntity<StreamingResponseBody> master(@PathVariable String contentUuid) {
        StreamingResponseBody body = out -> {
            try (var is = streamService.openMaster(contentUuid)) {
                is.transferTo(out);
            }
        };
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @Operation(summary = "Get HLS quality-specific playlist")
    @GetMapping(value = "/{contentUuid}/{quality}/playlist.m3u8", produces = M3U8_CONTENT_TYPE)
    public ResponseEntity<StreamingResponseBody> playlist(
            @PathVariable String contentUuid,
            @PathVariable String quality) {
        validate(quality);
        StreamingResponseBody body = out -> {
            try (var is = streamService.openPlaylist(contentUuid, quality)) {
                is.transferTo(out);
            }
        };
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @Operation(summary = "Get HLS video segment")
    @GetMapping(value = "/{contentUuid}/{quality}/{segment}", produces = TS_CONTENT_TYPE)
    public ResponseEntity<StreamingResponseBody> segment(
            @PathVariable String contentUuid,
            @PathVariable String quality,
            @PathVariable String segment) {
        validate(quality);
        validate(segment);
        StreamingResponseBody body = out -> {
            try (var is = streamService.openSegment(contentUuid, quality, segment)) {
                is.transferTo(out);
            }
        };
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .body(body);
    }

    private void validate(String param) {
        if (param.contains("..") || param.contains("/")) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid path parameter: " + param);
        }
    }
}