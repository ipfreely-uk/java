package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuardedSpliteratorTest {

    @Test
    void tryAdvance() {
        Family<V6> v6 = Family.v6();
        V6 guard = v6.fromUint(10);
        GuardedSpliterator<V6> iterator = new GuardedSpliterator<>(v6.min(), v6.max(), guard);

        List<V6> actual = new ArrayList<>();
        assertThrowsExactly(ExcessiveIterationException.class, () -> iterator.forEachRemaining(actual::add));
        assertEquals(guard, actual.get(actual.size() - 1));
    }

    @Test
    void trySplit() {
        Family<V6> v6 = Family.v6();
        V6 guard = v6.fromUint(10);
        GuardedSpliterator<V6> iterator = new GuardedSpliterator<>(v6.min(), v6.max(), guard);
        assertNull(iterator.trySplit());
    }
}
