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
        long c = Long.compareUnsigned(firstHigh, lastHigh);
        if (c > 0) {
            return -1;
        } else if ((c == 0) && (Long.compareUnsigned(firstLow, lastLow) > 0)) {
            return -1;
        }

        long highXor = firstHigh ^ lastHigh;
        if ((highXor & firstHigh) != 0) {
            return -1;
        }
        long lowXor = firstLow ^ lastLow;
        if ((lowXor & firstLow) != 0) {
            return -1;
        }

        int bitCount = Long.bitCount(lowXor) + Long.bitCount(highXor);
        int zeroes = Consts.V6_WIDTH - bitCount;
        int size = Long.numberOfLeadingZeros(highXor);
        if (size == Long.SIZE) {
            size += Long.numberOfLeadingZeros(lowXor);
        }
        if (size != zeroes) {
            return -1;
        }
        return size;
    }
}
