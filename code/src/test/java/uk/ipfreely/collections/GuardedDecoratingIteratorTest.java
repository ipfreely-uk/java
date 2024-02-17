package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.testing.GuardTester;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;

class GuardedDecoratingIteratorTest {

    @Test
    void next() {
        V4 zero = v4().min();
        Range<V4> internet = Ranges.block(zero, 0);

        {
            Iterator<V4> gdi = new GuardedDecoratingIterator<>(internet.iterator(), zero);

            assertTrue(gdi.hasNext());
            assertEquals(zero, gdi.next());
            assertThrowsExactly(ExcessiveIterationException.class, gdi::next);
        }
        {
            Iterator<V4> gdi = new GuardedDecoratingIterator<>(internet.iterator(), zero);

            GuardTester.test(gdi, 1);
        }
    }
}
