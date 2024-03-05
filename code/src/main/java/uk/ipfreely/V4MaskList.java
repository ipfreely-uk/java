// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.IntFunction;

import static uk.ipfreely.Validation.validate;

/**
 * A list of all possible Ip4 masks including 0
 */
class V4MaskList extends AbstractList<V4> implements RandomAccess {

    private static final int SIZE = Integer.SIZE + 1;

    static List<V4> MASKS = new V4MaskList(V4::fromInt);

    private final IntFunction<V4> source;

    V4MaskList(IntFunction<V4> source) {
        this.source = source;
    }

    @Override
    public V4 get(int index) {
        validate(index >= 0 && index < SIZE, "Index must be between 0 and " + SIZE, index, IndexOutOfBoundsException::new);
        int ip = 0;
        for (int i = 0; i < index; i++) {
            ip >>>= 1;
            ip |= 0b10000000_00000000_00000000_00000000;
        }
        return source.apply(ip);
    }

    @Override
    public int size() {
        return SIZE;
    }
}
