package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.testing.SpliteratorTester;

import java.util.ArrayList;
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
    void spliterator() {
        Family<V6> v6 = Family.v6();
        V6 guard = v6.fromUint(10);
        GuardedSpliterator<V6> s = new GuardedSpliterator<>(v6.min(), v6.max(), guard);
        SpliteratorTester.test(s);
    }
}
