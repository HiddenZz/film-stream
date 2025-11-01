package org.film.parser.feature.playlist.data.exceptions;

public class PlaylistNormalizeContentException extends RuntimeException {
    public PlaylistNormalizeContentException(String message) {
        super(message);
    }

    public PlaylistNormalizeContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
