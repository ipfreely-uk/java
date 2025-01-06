// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.util.Arrays;

final class V4Masking {
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
        int zeroes = Consts.V4_WIDTH - Integer.bitCount(xor);
        int size = Integer.numberOfLeadingZeros(xor);
        if (size != zeroes) {
            return -1;
        }
        return size;
    }
}
