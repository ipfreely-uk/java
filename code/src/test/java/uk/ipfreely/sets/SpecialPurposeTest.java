package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

class SpecialPurposeTest {

    private final V4 localhost4 = v4().parse("127.0.0.1");
    private final V6 localhost6 = v6().parse("::1");

    @Test
    void loopback() {
        {
            var set = SpecialPurpose.loopback(v4());
            assertTrue(set.contains(localhost4));
        }
        {
            var set = SpecialPurpose.loopback(v6());
            assertTrue(set.contains(localhost6));
            assertEquals(BigInteger.ONE, set.size());
        }
    }

    @Test
    void localUse() {
        {
            var set = SpecialPurpose.localUse(v4(), true);
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("192.168.100.10");
            assertTrue(set.contains(expected));
        }
        {
            var set = SpecialPurpose.localUse(v6(), true);
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("FD00::100");
            assertTrue(set.contains(expected));
            var reserved = v6().parse("FC00::100");
            assertFalse(set.contains(reserved));
        }
        {
            var set = SpecialPurpose.localUse(v6(), false);
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
            var set = SpecialPurpose.linkLocal(v4());
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("169.254.10.100");
            assertTrue(set.contains(expected));
        }
        {
            var set = SpecialPurpose.linkLocal(v6());
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("fe80::100");
            assertTrue(set.contains(expected));
        }
    }

    @Test
    void documentation() {
        {
            var set = SpecialPurpose.documentation(v4());
            assertFalse(set.contains(localhost4));
            var expected = v4().parse("198.51.100.10");
            assertTrue(set.contains(expected));
        }
        {
            var set = SpecialPurpose.documentation(v6());
            assertFalse(set.contains(localhost6));
            var expected = v6().parse("2001:db8::100");
            assertTrue(set.contains(expected));
        }
    }
}