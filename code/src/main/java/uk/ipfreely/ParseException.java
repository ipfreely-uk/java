package uk.ipfreely;

/**
 * Indicates the provided argument cannot be parsed to the expected type.
 */
public class ParseException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message reason
     */
    public ParseException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause root cause
     */
    public ParseException(final Throwable cause) {
        super(cause);
    }
}
