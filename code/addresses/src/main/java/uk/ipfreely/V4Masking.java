// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

final class V4Masking {

    private V4Masking() {}

    /**
     * @param first the first IP as an int
     * @param last  the last IP as an int
     * @return the mask size if this range can be a CIDR block or -1
     */
    static int maskSizeIfBlock(final int first, final int last) {
        int xor = first ^ last;

        if ((xor & first) != 0) {
            return -1;
        }
        if ((xor & last) != xor) {
            return -1;
        }

        int index = 0;
        while ((xor & 1) == 1) {
            xor >>>= 1;
            index++;
        }
        return xor == 0
                ? Consts.V4_WIDTH - index
                : -1;
    }
}
