package org.film.parser.feature.parser.playlist.data.exceptions;

public class ContentParseException extends ParseException {
    public ContentParseException(String message) {
        super(message);
    }

    public ContentParseException() {
        super("Can't parse content");
    }
}
