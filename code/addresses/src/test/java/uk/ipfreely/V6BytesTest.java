// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class V6BytesTest {

    @Test
    void toFromBytes() {
        long hi = 0x1234567890abcdefL;
        long lo = 0xfedcba0987654321L;
        byte[] bytes = V6Bytes.toBytes(hi, lo);
        R result = V6Bytes.fromBytes(R::new, bytes);

        Assertions.assertEquals(hi, result.hi);
        Assertions.assertEquals(lo, result.lo);
    }

    private static final class R {
        final long hi;
        final long lo;
        R(long hi, long lo) {
            this.hi = hi;
            this.lo = lo;
        }
    }
}