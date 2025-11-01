package org.film.parser.feature.playlist.data.exceptions;

public class SaveFileException extends RuntimeException {
    public SaveFileException(String message, String filename) {

        super(message);
        this.filename = filename;
    }

    public SaveFileException(String message, String filename, Throwable cause) {
        super(message, cause);
        this.filename = filename;
    }

    final String filename;
}
