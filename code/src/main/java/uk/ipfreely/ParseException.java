package uk.ipfreely;

/**
 * Indicates the provided argument cannot be parsed to the expected type.
 */
public class ParseException extends RuntimeException {

    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final Throwable cause) {
        super(cause);
    }
}
