package org.film.parser.feature.torrent.data.exceptions;

public class JackettException extends RuntimeException {
    public JackettException(String message) {
        super(message);
    }
    public JackettException(String message, Exception cause) {super(message, cause);}
}
