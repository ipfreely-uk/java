// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

final class Compare {

    private Compare() {}

    static <C extends Comparable<C>> C least(C a, C b) {
        return a.compareTo(b) <= 0
                ? a
                : b;
    }

    static <C extends Comparable<C>> C greatest(C a, C b) {
        return a.compareTo(b) >= 0
                ? a
                : b;
    }
}
