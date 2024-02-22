package uk.ipfreely.sets;

import java.util.StringJoiner;
import java.util.function.Function;


final class Validation {

    private Validation() {}

    static void validate(final boolean predicate, final String explanation, final byte[] actual, final Function<String, RuntimeException> ex) {
        if (!predicate) {
            StringJoiner joiner = new StringJoiner(" ");
            for (byte b : actual) {
                joiner.add("0x" + Integer.toHexString(b & 0xFF));
            }
            raise(explanation, joiner.toString(), ex);
        }
    }

    static void validate(final boolean predicate, final String explanation, final long actual, final Function<String, RuntimeException> ex) {
        if (!predicate) {
            raise(explanation, Long.toHexString(actual), ex);
        }
    }

    static void validate(final boolean predicate, final String explanation, final int actual, final Function<String, RuntimeException> ex) {
        if (!predicate) {
            raise(explanation, Integer.toHexString(actual), ex);
        }
    }

    static void validate(final boolean predicate, final String explanation, final Object actual, final Function<String, RuntimeException> ex) {
        if (!predicate) {
            raise(explanation, actual, ex);
        }
    }

    private static void raise(final String explanation, final Object actual, final Function<String, RuntimeException> ex) {
        throw ex.apply(explanation + "; got '" + actual + "'");
    }
}

