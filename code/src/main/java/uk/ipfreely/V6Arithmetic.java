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

//    static <T> T multiply(final V6Function<T> factory, final long h1, final long l1, final long h2, final long l2) {
//        long LONG_MASK = 0xFFFFFFFFL;
//        int[] a1 = {i(h1, 32), i(h1, 0), i(l1, 32), i(l1, 0)};
//        int[] a2 = {i(h2, 32), i(h2, 0), i(l2, 32), i(l2, 0)};
//        int[] result = multiplyToLen(a1, a1.length, a2, a2.length, null);
//        long high = ((result[result.length - 4] & LONG_MASK) << 32) + (result[result.length - 3] & LONG_MASK);
//        long low = ((result[result.length - 2] & LONG_MASK) << 32) + (result[result.length - 1] & LONG_MASK);
//        return factory.apply(high, low);
//    }
//
//
//    private static int[] multiplyToLen(int[] x, int xlen, int[] y, int ylen, int[] z) {
//        long LONG_MASK = 0xFFFFFFFFL;
//
//        int xstart = xlen - 1;
//        int ystart = ylen - 1;
//
//        if (z == null || z.length < (xlen+ ylen))
//            z = new int[xlen+ylen];
//
//        long carry = 0;
//        for (int j=ystart, k=ystart+1+xstart; j >= 0; j--, k--) {
//            long product = (y[j] & LONG_MASK) *
//                    (x[xstart] & LONG_MASK) + carry;
//            z[k] = (int)product;
//            carry = product >>> 32;
//            print(z, k, "product", "y", j, "x", xstart, "+carry");
//            print(z, k, "set", k, "product");
//            print(z, k, "set carry product >>> 32");
//        }
//        print(z, xstart, "set", xstart, "carry");
//        z[xstart] = (int)carry;
//
//        for (int i = xstart-1; i >= 0; i--) {
//            System.out.println("carry=0");
//            print(z, i, "set", "carry=0");
//            carry = 0;
//            for (int j=ystart, k=ystart+1+i; j >= 0; j--, k--) {
//                long product = (y[j] & LONG_MASK) *
//                        (x[i] & LONG_MASK) +
//                        (z[k] & LONG_MASK) + carry;
//                print(z, k, "product", "y", j, "x", i, "+carry", "+", "z", k);
//                print(z, k, "set", k, "product");
//                z[k] = (int)product;
//                print(z, k, "set carry product >>> 32");
//                carry = product >>> 32;
//            }
//            print(z, i, "set", i, "carry");
//            z[i] = (int)carry;
//        }
//        return z;
//    }
//
//    private static void print(int[] z, int idx, Object... msg) {
//        int min = z.length - 4;
//        if (idx >= min) {
//            String s = Stream.of(msg).map(Objects::toString).collect(Collectors.joining(" "));
//            System.out.println(idx + ">>" + s);
//        }
//    }

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
}
