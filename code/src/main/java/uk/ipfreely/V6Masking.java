// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

final class V6Masking {

    private V6Masking() {}

    /**
     * @param firstHigh the high bits of the first IP
     * @param firstLow  the low bits of the first IP
     * @param lastHigh  the high bits of the last IP
     * @param lastLow   the low bits of the last IP
     * @return the block mask size if this range can be a CIDR block or -1
     */
    static int maskSizeIfBlock(final long firstHigh, final long firstLow, final long lastHigh, final long lastLow) {
        int lowMask = maskSizeIfBlock(firstLow, lastLow);
        if (lowMask < 0) {
            return -1;
        }
        if (firstHigh == lastHigh) {
            return V6Consts.WIDTH - lowMask;
        }
        if (lowMask == 64) {
            int highMask = maskSizeIfBlock(firstHigh, lastHigh);
            if (highMask < 0) {
                return -1;
            }
            return V6Consts.WIDTH - (highMask + lowMask);
        }
        return -1;
    }

    /**
     * @param first the bits of the first IP
     * @param last  the bits of the last IP
     * @return the mask size or -1 for no mask
     */
    private static int maskSizeIfBlock(final long first, final long last) {
        long xor = first ^ last;

        if ((xor & first) != 0) {
            return -1;
        }
        if ((xor & last) != xor) {
            return -1;
        }

        int count = 0;
        while ((xor & 1) == 1) {
            xor >>>= 1;
            count++;
        }
        return (xor == 0) ? count : -1;
    }
}
