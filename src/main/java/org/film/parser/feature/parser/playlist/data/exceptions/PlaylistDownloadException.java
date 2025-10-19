package org.film.parser.feature.parser.playlist.data.exceptions;

public class PlaylistDownloadException extends RuntimeException {
    public PlaylistDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaylistDownloadException(String message) {
        super(message);
    }
}
