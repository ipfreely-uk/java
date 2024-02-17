package uk.ipfreely.examples;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.collections.Range;
import uk.ipfreely.collections.Ranges;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomAddressTest {

    private static final Random RAN = new Random(0);

    @Test
    void generate() {
        {
            Address<V4> address = RandomAddress.generate(Family.v4(), RAN::nextInt);
            assertNotNull(address);
        }
        {
            Address<V6> address = RandomAddress.generate(Family.v6(), RAN::nextInt);
            assertNotNull(address);
        }
        {
            int actual = 3000;
            Address<V4> address = RandomAddress.generate(Family.v4(), () -> actual);
            assertEquals(BigInteger.valueOf(actual), address.toBigInteger());
        }
        {
            int actual = 3000;
            Iterator<Integer> randoms = Arrays.asList(0, 0, 0, actual).iterator();
            Address<V6> address = RandomAddress.generate(Family.v6(), randoms::next);
            assertEquals(BigInteger.valueOf(actual), address.toBigInteger());
        }
    }

    @Test
    void from() {
        {
            Range<V4> range = Ranges.from(Family.v4().min(), Family.v4().max());
            Address<V4> address = RandomAddress.from(range, RAN::nextInt);
            assertTrue(range.contains(address));
        }
    }
}
