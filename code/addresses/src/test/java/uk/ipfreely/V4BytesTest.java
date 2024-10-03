// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class V4BytesTest {

    @Test
    void testUint32Bytes() {
        byte[] arr = { (byte) 0xFF, 1, 2, 3 };
        int actual = V4Bytes.fromBytes(arr);
        byte[] rev = V4Bytes.toBytes(actual);

        assertEquals(0xFF010203, actual);
        assertArrayEquals(arr, rev);
    }
}
