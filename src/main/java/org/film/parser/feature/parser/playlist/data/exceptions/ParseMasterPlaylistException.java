package org.film.parser.feature.parser.playlist.data.exceptions;

public class ParseMasterPlaylistException extends ParseException {


    public ParseMasterPlaylistException() {
        super("Can't parse master playlist");
    }

    public ParseMasterPlaylistException(String message) {
        super(message);
    }
}
