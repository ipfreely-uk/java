// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class V4MaskingTest {

    @Test
    void maskSizeIfBlock() {
        assertEquals(-1, V4Masking.maskSizeIfBlock(0b1, 0b11));
        assertEquals(-1, V4Masking.maskSizeIfBlock(0b011, 0b100));
        assertEquals(-1, V4Masking.maskSizeIfBlock(0b1011, 0b1100));
        assertEquals(0, V4Masking.maskSizeIfBlock(0x0, 0xFFFFFFFF));
        assertEquals(32, V4Masking.maskSizeIfBlock(0x0, 0x0));
        assertEquals(24, V4Masking.maskSizeIfBlock(0xCAFEBB00, 0xCAFEBBFF));
        assertEquals(16, V4Masking.maskSizeIfBlock(0xFFFF0000, 0xFFFFFFFF));
        assertEquals(-1, V4Masking.maskSizeIfBlock(0xABFF0000, 0xFFFFFFFF));
        assertEquals(32, V4Masking.maskSizeIfBlock(0xFFFFFFFF, 0xFFFFFFFF));
    }
}