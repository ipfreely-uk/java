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

    static <T> T multiply(final V6Function<T> factory, final long h1, final long l1, final long h2, final long l2) {
        int x0 = i(h1, 32);
        int x1 = i(h1, 0);
        int x2 = i(l1, 32);
        int x3 = i(l1, 0);

        int y0 = i(h2, 32);
        int y1 = i(h2, 0);
        int y2 = i(l2, 32);
        int y3 = i(l2, 0);

        int z7;
        int z6;
        int z5;
        int z4;

        long product;
        long carry = 0;
        product = m(y3, x3, carry);
        z7 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y2, x3, carry);
        z6 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y1, x3, carry);
        z5 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y0, x3, carry);
        z4 = (int) product;

        carry = 0;
        product = m(y3, x2, carry, z6);
        z6 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y2, x2, carry, z5);
        z5 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y1, x2, carry, z4);
        z4 = (int) product;

        carry = 0;
        product = m(y3, x1, carry, z5);
        z5 = (int) product;
        carry = product >>> Integer.SIZE;
        product = m(y2, x1, carry, z4);
        z4 = (int) product;

        carry = 0;
        product = m(y3, x0, carry, z4);
        z4 = (int) product;

        final long M = 0xFFFFFFFFL;
        long high = ((z4 & M) << Integer.SIZE) + (z5 & M);
        long low = ((z6 & M) << Integer.SIZE) + (z7 & M);

        return factory.apply(high, low);
    }

    private static long m(int y, int x, long carry, int zprev) {
        final long M = 0xFFFFFFFFL;
        return (M & y) * (M & x) + carry + (M & zprev);
    }

    private static long m(int y, int x, long carry) {
        final long M = 0xFFFFFFFFL;
        return (M & y) * (M & x) + carry;
    }

    private static int i(long l, int shift) {
        return (int) (l >>> shift);
    }

    static double doubleValue(long high, long low) {
        if (high == 0 && low >= 0) {
            return (double) low;
        }

        final long LONG_MASK = 0xFFFFFFFFL;
        final int SIGNIFICAND_WIDTH = 53;
        final long SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL;
        final int EXP_BIAS = 1023;

        int mag0;
        int mag1;
        int mag2;

        int magLen;
        if (high == 0) {
            magLen = 2;
            mag0 = i(low, Integer.SIZE);
            mag1 = i(low, 0);
            mag2 = 0;
        } else if ((high & 0xFFFFFFFF_00000000L) != 0) {
            magLen = 4;
            mag0 = i(high, Integer.SIZE);
            mag1 = i(high, 0);
            mag2 = i(low, Integer.SIZE);
        } else {
            magLen = 3;
            mag0 = i(high, 0);
            mag1 = i(low, Integer.SIZE);
            mag2 = i(low, 0);
        }

        int lowestSetBit;
        if (low == 0) {
            lowestSetBit = Long.SIZE - Long.numberOfTrailingZeros(high) + Long.SIZE;
        } else {
            lowestSetBit = Long.SIZE - Long.numberOfTrailingZeros(low);
        }

        int bitLengthForInt = Integer.SIZE - Integer.numberOfLeadingZeros(mag0);
        int exponent = ((magLen - 1) << 5) + bitLengthForInt - 1;

        int shift = exponent - SIGNIFICAND_WIDTH;

        int nBits = shift & 0x1f;
        int nBits2 = 32 - nBits;

        int highBits;
        int lowBits;
        if (nBits == 0) {
            highBits = mag0;
            lowBits = mag0;
        } else {
            highBits = mag0 >>> nBits;
            lowBits = (mag0 << nBits2) | (mag1 >>> nBits);
            if (highBits == 0) {
                highBits = lowBits;
                lowBits = (mag1 << nBits2) | (mag2 >>> nBits);
            }
        }

        long twiceSignifFloor = ((highBits & LONG_MASK) << 32) | (lowBits & LONG_MASK);

        long signifFloor = twiceSignifFloor >> 1;
        signifFloor &= SIGNIF_BIT_MASK;

        boolean increment = (twiceSignifFloor & 1) != 0 && ((signifFloor & 1) != 0 || lowestSetBit < shift);
        long signifRounded = increment ? signifFloor + 1 : signifFloor;
        long bits = (long) ((exponent + EXP_BIAS)) << (SIGNIFICAND_WIDTH - 1);
        bits += signifRounded;
        return Double.longBitsToDouble(bits);
    }
}
