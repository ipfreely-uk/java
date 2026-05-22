// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.StringJoiner;
import java.util.function.Function;

final class Validation {

    private Validation() {}

    static <T extends Throwable> void validate(final boolean predicate, final String explanation, final byte[] actual, final Function<String, T> ex) throws T {
        if (!predicate) {
            var joiner = new StringJoiner(" ");
            for (byte b : actual) {
                joiner.add("0x" + Integer.toHexString(b & Consts.BYTE_MASK));
            }
            raise(explanation, joiner.toString(), ex);
        }
    }

    static <T extends Throwable> void validate(final boolean predicate, final String explanation, final long actual, final Function<String, T> ex) throws T {
        if (!predicate) {
            raise(explanation, Long.toHexString(actual), ex);
        }
    }

    static <T extends Throwable> void validate(final boolean predicate, final String explanation, final int actual, final Function<String, T> ex) throws T {
        if (!predicate) {
            raise(explanation, Integer.toHexString(actual), ex);
        }
    }

    static <T extends Throwable> void validate(final boolean predicate, final String explanation, final Object actual, final Function<String, T> ex) throws T {
        if (!predicate) {
            raise(explanation, actual, ex);
        }
    }

    private static <T extends Throwable> void raise(final String explanation, final Object actual, final Function<String, T> ex) throws T {
        throw ex.apply(explanation + "; got '" + actual + "'");
    }
}

