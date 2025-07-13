// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class V6BytesTest {

    @Test
    void fallOff() {
        assertThrowsExactly(AssertionError.class, () -> V6Bytes.toLong(0, 1000));
    }

    @Test
    void toFromBytes() {
        long hi = 0x1234567890abcdefL;
        long lo = 0xfedcba0987654321L;
        byte[] bytes = V6Bytes.toBytes(hi, lo);
        R result = V6Bytes.fromBytes(R::new, bytes);

        assertEquals(hi, result.hi);
        assertEquals(lo, result.lo);
    }

    private record R(long hi, long lo) {
    }
}