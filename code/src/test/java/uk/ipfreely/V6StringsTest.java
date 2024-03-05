// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class V6StringsTest {

    @Test
    void toIpv6String() {
        assertEquals("::", V6Strings.toIpv6String(0, 0));
        assertEquals("f::1", V6Strings.toIpv6String(0xf_0000_0000_0000L, 1));
        assertEquals("ff::1", V6Strings.toIpv6String(0xff_0000_0000_0000L, 1));
        assertEquals("fff::1", V6Strings.toIpv6String(0xfff_0000_0000_0000L, 1));
        assertEquals("ffff::1", V6Strings.toIpv6String(0xffff_0000_0000_0000L, 1));
        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", V6Strings.toIpv6String(0xffffffffffffffffL, 0xffffffffffffffffL));
        assertEquals("dead:beef:cafe:babe:dead:beef:cafe:babe", V6Strings.toIpv6String(0xdeadbeefcafebabeL, 0xdeadbeefcafebabeL));
        assertEquals("1:2:3:4:5:678:9:0", V6Strings.toIpv6String(0x1_0002_0003_0004L, 0x5_0678_0009_0000L));
        assertEquals("1234:2::9:0:0", V6Strings.toIpv6String(0x1234_0002_0000_0000L, 0x9_0000_0000L));
    }
}
