// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import java.util.function.Function;

final class Validation {

    private Validation() {}

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
