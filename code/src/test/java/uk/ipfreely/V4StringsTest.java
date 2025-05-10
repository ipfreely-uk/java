// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V4StringsTest {

    @Test
    void to() {
        assertEquals("0.0.0.0", V4Strings.to(0));
        assertEquals("255.255.255.255", V4Strings.to(0xFFFFFFFF));
        assertEquals("0.0.0.1", V4Strings.to(1));
        assertEquals("255.0.0.0", V4Strings.to(0xFF000000));
        assertEquals("10.0.0.0", V4Strings.to(0xA000000));
    }

    @Test
    void from() {
        assertEquals(0, V4Strings.from("0.0.0.0"));
        assertEquals(0xFFFFFFFF, V4Strings.from("255.255.255.255"));
        assertEquals(0xFF000001, V4Strings.from("255.0.0.1"));
        assertEquals(0x0A096401, V4Strings.from("10.9.100.1"));

        assertThrowsExactly(ParseException.class, () -> V4Strings.from("01.0.0.0"));
    }
}