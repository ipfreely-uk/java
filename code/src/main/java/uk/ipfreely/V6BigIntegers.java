// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;

final class V6BigIntegers {

    private V6BigIntegers() {}

    static BigInteger toBigInteger(long high, long low) {
        if (high == 0L && low >= 0L) {
            return BigInteger.valueOf(low);
        }
        final byte[] barr = V6Bytes.toBytes(high, low);
        return new BigInteger(1, barr);
    }

    static <T> T fromBigInteger(final V6Function<T> factory, final BigInteger ip) {
        final int bytes = Consts.V6_WIDTH / Byte.SIZE;
        final int half = bytes / 2;

        final long high;
        final long low;

        byte[] arr = ip.toByteArray();
        final int offset = arr.length - bytes;

        if (offset >= 0) {
            high = V6Bytes.toLong(offset, half, arr);
            low = V6Bytes.toLong(offset + half, half, arr);
        } else if (arr.length <= half) {
            high = 0;
            low = V6Bytes.toLong(0, arr.length, arr);
        } else {
            final int off2 = arr.length - half;
            high = V6Bytes.toLong(0, off2, arr);
            low = V6Bytes.toLong(off2, half, arr);
        }

        return factory.apply(high, low);
    }
}
