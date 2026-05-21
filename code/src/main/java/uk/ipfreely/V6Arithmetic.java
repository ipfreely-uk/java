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

    static <T> T divide(V6Function<T> factory, final long h0, final long l0, final long h1, final long l1, final boolean modulus) {
        // long division algo

        final int offset = Long.numberOfLeadingZeros(h0);

        // quotient
        long qh = 0;
        long ql = 0;
        // remainder
        long rh = 0;
        long rl = 0;
        // divide
        for (int i = Consts.V6_WIDTH - offset - 1; i >= 0; i--) {
            // r << 1
            long x = rl >>> (Long.SIZE - 1);
            rh = (rh << 1) | x;
            rl = rl << 1;
            // r[0] = n[i]
            long bh = 0;
            long bl = 0;
            int high = i - Long.SIZE;
            if (high >= 0) {
                bh = 1L << high;
            } else {
                bl = 1L << i;
            }
            long nh = h0 & bh;
            long nl = l0 & bl;
            if (nh != 0 || nl != 0) {
                rl |= 1;
            }
            // if r >= d
            int cu = Long.compareUnsigned(rh, h1);
            if (cu == 0) {
                cu = Long.compareUnsigned(rl, l1);
            }
            if (cu >= 0) {
                // r = r - d
                rh = rh - h1;
                if (Long.compareUnsigned(rl, l1) < 0) {
                    rh--;
                }
                rl = rl - l1;
                // q[i] = 1
                qh |= bh;
                ql |= bl;
            }
        }

        if (modulus) {
            return factory.apply(rh, rl);
        }
        return factory.apply(qh, ql);
    }
}
