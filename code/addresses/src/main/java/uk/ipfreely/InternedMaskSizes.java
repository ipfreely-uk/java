// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Holds network sizes for given CIDR mask bits.
 */
final class InternedMaskSizes {

    private static final BigInteger[] SIZES = IntStream.rangeClosed(0, Consts.V6_WIDTH)
            .mapToObj(InternedMaskSizes::gen)
            .toArray(BigInteger[]::new);

    private InternedMaskSizes() {
    }

    private static BigInteger gen(int n) {
        return BigInteger.valueOf(2).pow(Consts.V6_WIDTH - n);
    }

    static BigInteger v4(int maskBits) {
        int offset = Consts.V6_WIDTH - Consts.V4_WIDTH + maskBits;
        return SIZES[offset];
    }

    static BigInteger v6(int maskBits) {
        return SIZES[maskBits];
    }
}
