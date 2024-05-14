package com.verygoodbank.tes.web.transform;

/**
 * Throw if the parsed file is invalid
 */
public class ParsingException extends Exception {
    public ParsingException(String message) {
        super(message);
    }
}
