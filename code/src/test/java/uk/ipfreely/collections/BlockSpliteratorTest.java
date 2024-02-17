package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.math.BigInteger;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BlockSpliteratorTest {

    @Test
    void tryAdvance() {
        {
            Range<V4> subnet = Ranges.parseCidr(Family.v4(), "192.168.0.0/24");
            AtomicReference<Block<V4>> actual = new AtomicReference<>();

            BlockSpliterator<V4> sb = new BlockSpliterator<>(subnet.first(), subnet.last().next());
            boolean acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(subnet, actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(BigInteger.ONE, actual.get().size());
            assertEquals(subnet.last().next(), actual.get().first());

            acquired = sb.tryAdvance(actual::set);
            assertFalse(acquired);
        }
        {
            Range<V6> big = Ranges.parseCidr(Family.v6(), "dead::/16");
            AtomicReference<Block<V6>> actual = new AtomicReference<>();

            BlockSpliterator<V6> sb = new BlockSpliterator<>(big.first(), big.last().next());
            boolean acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(big, actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(BigInteger.ONE, actual.get().size());
            assertEquals(big.last().next(), actual.get().first());

            acquired = sb.tryAdvance(actual::set);
            assertFalse(acquired);
        }
    }

    @Test
    void trySplit() {
        Range<V4> subnet = Ranges.parseCidr(Family.v4(), "192.168.0.0/24");
        AtomicReference<Block<V4>> actual = new AtomicReference<>();

        BlockSpliterator<V4> sb = new BlockSpliterator<>(subnet.first(), subnet.last().next());
        Spliterator<Block<V4>> split = sb.trySplit();

        assertNull(split);
    }

    @Test
    void estimateSize() {
    }

    @Test
    void characteristics() {
    }
}