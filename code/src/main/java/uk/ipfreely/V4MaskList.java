// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.AbstractList;
import java.util.RandomAccess;
import java.util.function.IntFunction;

/**
 * A list of all possible Ip4 masks including 0
 */
final class V4MaskList extends AbstractList<V4> implements RandomAccess {
    private static final int SIZE = Consts.V4_WIDTH + 1;

    private final V4[] masks = new V4[SIZE];

    V4MaskList(IntFunction<V4> source) {
        for (int i = 0; i < masks.length; i++) {
            masks[i] = resolve(source, i);
        }
    }

    @Override
    public V4 get(int index) {
        return masks[index];
    }

    @Override
    public int size() {
        return SIZE;
    }

    private static V4 resolve(IntFunction<V4> source, int index) {
        int ip = 0;
        for (int i = 0; i < index; i++) {
            ip >>>= 1;
            ip |= 0b10000000_00000000_00000000_00000000;
        }
        return source.apply(ip);
    }
}
