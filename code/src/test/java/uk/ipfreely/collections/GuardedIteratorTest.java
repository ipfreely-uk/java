package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V6;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GuardedIteratorTest {

    @Test
    void guard() {
        Family<V6> v6 = Family.v6();
        V6 guard = v6.fromUint(10);
        GuardedIterator<V6> iterator = new GuardedIterator<>(v6.min(), v6.max(), guard);

        List<V6> actual = new ArrayList<>();
        assertThrowsExactly(ExcessiveIterationException.class, () -> iterator.forEachRemaining(actual::add));
        assertEquals(guard, actual.get(actual.size() - 1));
    }
}