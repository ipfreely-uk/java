// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.Arrays;

final class V4Masking {
    private static final int[] XORS = allPossible();

    private V4Masking() {}

    /**
     * @param first the first IP as an int
     * @param last  the last IP as an int
     * @return the mask size if this range can be a CIDR block or -1
     */
    static int maskSizeIfBlock(final int first, final int last) {
        if (Integer.compareUnsigned(first, last) > 0) {
            return -1;
        }
        int xor = first ^ last;
        if ((xor & first) != 0) {
            return -1;
        }
        if (xor == 0xFFFFFFFF) {
            // special case because it's negative
            return 0;
        }
        int idx = Arrays.binarySearch(XORS, xor);
        return (idx < 0)
                ? -1
                : Consts.V4_WIDTH - idx;
    }

    private static int[] allPossible() {
        int[] result = new int[Consts.V4_WIDTH];
        for (int next = 0, i = 0; i < result.length; i++) {
            result[i] = next;
            next <<= 1;
            next |= 1;
        }
        return result;
    }
}
