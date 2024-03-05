// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

final class V6Arithmetic {

    private V6Arithmetic() {}

    static <T> T add(final V6Function<T> factory, final long h1, final long l1, final long h2, final long l2) {
        final long low = l1 + l2;
        final boolean incHigh = Long.compareUnsigned(low, l1) < 0 && Long.compareUnsigned(low, l2) < 0;
        long high = incHigh
                ? h1 + h2 + 1
                : h1 + h2;
        return factory.apply(high, low);
    }

    static <T> T subtract(final V6Function<T> factory, final long h1, final long l1, final long h2, final long l2) {
        final long low = l1 - l2;
        final long high = Long.compareUnsigned(l1, l2) < 0
                ? h1 - h2 - 1
                : h1 - h2;
        return factory.apply(high, low);
    }

}
