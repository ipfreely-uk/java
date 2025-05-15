// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;
import uk.ipfreely.testing.Addresses;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

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
        assertEquals("::ffff:255.255.255.255", V6Strings.toIpv6String(0, 0xFFFF_FF_FF_FF_FFL));
        assertEquals("::ffff:0.0.0.0", V6Strings.toIpv6String(0, 0xFFFF_00_00_00_00L));
    }

    @Test
    void parse() {
        {
            N expected = new N(0x1000200030004000L, 0x5000600070008000L);
            N actual = V6Strings.parse("1000:2000:3000:4000:5000:6000:7000:8000", N::new);
            assertEquals(expected, actual);
        }
        {
            N expected = new N(0xCAFEBABEDEADBEEFL, 0xF001);
            N actual = V6Strings.parse("CAFE:BABE:DEAD:BEEF::F001", N::new);
            assertEquals(expected, actual);
        }
        {
            N expected = new N(0, 0);
            N actual = V6Strings.parse("::", N::new);
            assertEquals(expected, actual);
        }
        {
            for (String s : Addresses.valid(Family.v6())) {
                V6Strings.parse(s, N::new);
            }
        }
        {
            for (String s : Addresses.invalid(Family.v6())) {
                assertThrowsExactly(ParseException.class, () -> V6Strings.parse(s, N::new), s);
            }
        }
    }

    @Test
    void parse4in6() {
        {
            N expected = new N(0, 0xD014403L);
            N actual = V6Strings.parse4In6("::13.1.68.3", N::new);
            assertEquals(expected, actual);
        }
        {
            N expected = new N(0, 0xFFFF81903426L);
            N actual = V6Strings.parse4In6("0:0:0:0:0:FFFF:129.144.52.38", N::new);
            assertEquals(expected, actual);
        }
    }

    private static class N {
        final long high;
        final long low;

        private N(long high, long low) {
            this.high = high;
            this.low = low;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            N n = (N) o;
            return high == n.high && low == n.low;
        }

        @Override
        public int hashCode() {
            return Objects.hash(high, low);
        }

        @Override
        public String toString() {
            return String.format("%1$016X%2$016X", high, low);
        }
    }
}
