package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.testing.EqualsTester;
import uk.ipfreely.testing.GuardTester;

import java.math.BigInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

class AddressSetsTest {

    @Test
    void of() {
        Range<V4> zero = Ranges.address(v4().fromUint(0));
        Range<V4> ten = Ranges.address(v4().fromUint(10));
        Range<V4> oneToNine = Ranges.from(zero.first().next(), ten.first().prev());

        {
            AddressSet<V4> actual = AddressSets.of(zero, ten, oneToNine);

            assertEquals(1, actual.ranges().count());
            assertTrue(actual.contains(zero.first()));
            assertTrue(actual.contains(ten.first()));
            assertEquals(BigInteger.valueOf(11), actual.size());
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, ten);
            AddressSet<V4> reverse = AddressSets.of(ten, zero);

            assertEquals(2, actual.ranges().count());
            assertEquals(reverse, actual);
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, Ranges.address(v4().fromUint(0)));

            assertEquals(1, actual.ranges().count());
            assertEquals(BigInteger.ONE, actual.size());
        }
    }

    @Test
    void equality() {
        Range<V4> zero = Ranges.address(v4().fromUint(0));
        Range<V4> ten = Ranges.address(v4().fromUint(10));
        Range<V4> oneToNine = Ranges.from(zero.first().next(), ten.first().prev());

        EqualsTester.test(
                new Object(),
                AddressSets.of(zero),
                AddressSets.of(ten),
                AddressSets.of(),
                AddressSets.of(oneToNine),
                AddressSets.of(ten, zero),
                AddressSets.of(zero, ten),
                AddressSets.of(zero),
                AddressSets.of(ten, oneToNine, zero)
        );
    }

    @Test
    void empty() {
        AddressSet<V4> empty = AddressSets.of();
        assertEquals(BigInteger.ZERO, empty.size());
        assertFalse(empty.iterator().hasNext());
        assertEquals(0L, empty.ranges().count());
    }

    @Test
    void guarded() {
        Range<V6> internet = Ranges.block(v6().min(), 0);
        AddressSet<V6> everything = AddressSets.of(internet);
        V6 zero = v6().min();

        {
            AddressSet<V6> actual = AddressSets.guarded(everything, zero);

            assertTrue(actual.getClass().getName().contains("Guarded"));

            GuardTester.test(actual.iterator(), 1);
            GuardTester.test(actual.spliterator(), 1);
            GuardTester.test(actual.ranges(), 1);

            assertTrue(actual.contains(zero));
            assertTrue(actual.contains(v6().max()));
        }
        {

        }
    }
}