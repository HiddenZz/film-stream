package org.film.parser.feature.playlist.data.exceptions;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException(String message, String filename) {
        super(message);
        this.filename = filename;
    }

    public NoSuchFileException(String message, String filename, Throwable cause) {
        super(message, cause);
        this.filename = filename;
    }

    final String filename;
}
