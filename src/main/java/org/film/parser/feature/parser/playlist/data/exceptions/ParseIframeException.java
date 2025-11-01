package org.film.parser.feature.parser.playlist.data.exceptions;

public class ParseIframeException extends ParseException {

    public ParseIframeException() {
        super("Can't parse iframe");
    }

    public ParseIframeException(String message) {
        super(message);
    }
}
