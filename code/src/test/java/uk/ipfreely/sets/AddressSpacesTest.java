package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

class AddressSpacesTest {

    private final V4 localhost4 = v4().parse("127.0.0.1");
    private final V6 localhost6 = v6().parse("::1");

    @Test
    void loopback() {
        {
            var set = AddressSpaces.loopback(v4());
            assertTrue(set.contains(localhost4));
        }
        {
            var set = AddressSpaces.loopback(v6());
            assertTrue(set.contains(localhost6));
            assertEquals(BigInteger.ONE, set.size());
        }
    }

    @Test
    void uniqueLocal() {
        {
            var set = AddressSpaces.uniqueLocal(v4(), true);
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("192.168.100.10");
            assertTrue(set.contains(expected));
        }
        {
            var set = AddressSpaces.uniqueLocal(v6(), true);
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("FD00::100");
            assertTrue(set.contains(expected));
            var reserved = v6().parse("FC00::100");
            assertFalse(set.contains(reserved));
        }
        {
            var set = AddressSpaces.uniqueLocal(v6(), false);
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("FD00::100");
            assertTrue(set.contains(expected));
            var reserved = v6().parse("FC00::100");
            assertTrue(set.contains(reserved));
        }
    }

    @Test
    void linkLocal() {
        {
            var set = AddressSpaces.linkLocal(v4());
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("169.254.10.100");
            assertTrue(set.contains(expected));
        }
        {
            var set = AddressSpaces.linkLocal(v6());
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("fe80::100");
            assertTrue(set.contains(expected));
        }
    }

    @Test
    void documentation() {
        {
            var set = AddressSpaces.documentation(v4());
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("198.51.100.10");
            assertTrue(set.contains(expected));
        }
        {
            var set = AddressSpaces.documentation(v6());
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("2001:db8::100");
            assertTrue(set.contains(expected));
        }
    }

    @Test
    void multicast() {
        {
            var set = AddressSpaces.multicast(v4());
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("239.255.255.255");
            assertTrue(set.contains(expected));
        }
        {
            var set = AddressSpaces.multicast(v6());
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("ff00::100");
            assertTrue(set.contains(expected));
        }
    }
}