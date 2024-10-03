// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
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
