// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.RandomAccess;

/**
 * A list of all possible Ip6 masks including 0
 */
final class V6MaskList extends AbstractList<V6> implements RandomAccess {
    private static final int SIZE = Consts.V6_WIDTH + 1;
    private static final int FIRST_LOW_IDX = SIZE / 2;

    private final V6[] masks = new V6[SIZE];

    V6MaskList(V6Function<V6> source) {
        for (int i = 0; i < masks.length; i++) {
            masks[i] = resolve(source, i);
        }
    }

    @Override
    public V6 get(int index) {
        return masks[index];
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public int indexOf(Object o) {
        int idx = -1;
        if (o instanceof V6) {
            V6 a = (V6) o;
            idx = Arrays.binarySearch(masks, a, V6::compareTo);
        }
        return idx < 0 ? -1 : idx;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private static long nth(int index) {
        long ip = 0;
        for (int i = 0; i < index; i++) {
            ip >>>= 1;
            ip |= 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        }
        return ip;
    }

    private static V6 resolve(V6Function<V6> source, int index) {
        if (index < FIRST_LOW_IDX) {
            return source.apply(nth(index), 0L);
        }
        return source.apply(0xffffffffffffffffL, nth(index - FIRST_LOW_IDX));
    }
}
