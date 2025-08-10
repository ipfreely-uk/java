package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;
import static uk.ipfreely.sets.AddressSets.parseCidr;

class AddressSetSpliteratorTest {

    private final AddressSet<V4> small = AddressSets.of(
            parseCidr(v4(), "192.168.0.0/24"),
            parseCidr(v4(), "10.168.0.0/24")
    );

    private final AddressSet<V6> medium = AddressSets.of(
            parseCidr(v6(), "::/67"),
            parseCidr(v6(), "1::/67"),
            parseCidr(v6(), "2::/67"),
            parseCidr(v6(), "3::/67")
    );

    private final AddressSet<V6> massive = AddressSets.of(
            parseCidr(v6(), "::/56"),
            parseCidr(v6(), "ffff::/56")
    );

    @Test
    void tryAdvance() {
        {
            var expected = toList(small);
            var split = AddressSetSpliterator.consume(small.ranges());
            var actual = toList(split);
            assertEquals(expected, actual);
        }
    }

    @Test
    void trySplit() {
        {
            var expected = toList(small);
            var tail = AddressSetSpliterator.consume(small.ranges());
            var head = tail.trySplit();
            var actual = toList(head, tail);
            assertEquals(expected, actual);
        }
        {
            var empty = AddressSets.of();
            var nothing = AddressSetSpliterator.consume(empty.ranges());
            var head = nothing.trySplit();
            assertNull(head);
        }
        {
            var nothing = AddressSetSpliterator.consume(medium.ranges());
            var head = nothing.trySplit();
            assertNotNull(head);
        }
        {
            var set = AddressSets.of(
                    parseCidr(v4(), "192.168.0.0/24"),
                    parseCidr(v4(), "192.168.1.0/24"),
                    parseCidr(v4(), "192.168.2.0/24"),
                    parseCidr(v4(), "192.168.3.0/24"),
                    parseCidr(v4(), "192.168.4.0/24"),
                    parseCidr(v4(), "192.168.5.0/24"),
                    parseCidr(v4(), "192.168.6.0/24"),
                    parseCidr(v4(), "192.168.7.0/24"),
                    parseCidr(v4(), "192.168.8.0/24")
                    );
            var tail = AddressSetSpliterator.consume(set.ranges());
            var head = tail.trySplit();
            assertNotNull(head);
            var expected = toList(set);
            var actual = toList(head, tail);
            assertEquals(expected, actual);
        }
    }

    @Test
    void estimateSize() {
        {
            int expected = toList(small).size();
            var split = AddressSetSpliterator.consume(small.ranges());
            long actual = split.estimateSize();
            assertEquals(expected, actual);
        }
        {
            var split = AddressSetSpliterator.consume(medium.ranges());
            long actual = split.estimateSize();
            assertEquals(Long.MAX_VALUE, actual);
        }
        {
            var split = AddressSetSpliterator.consume(massive.ranges());
            long actual = split.estimateSize();
            assertEquals(Long.MAX_VALUE, actual);
        }
        {
            var empty = AddressSets.of();
            var nothing = AddressSetSpliterator.consume(empty.ranges());
            assertEquals(0, nothing.estimateSize());
        }
    }

    @Test
    void characteristics() {
        {
            var split = AddressSetSpliterator.consume(small.ranges());
            long actual = split.characteristics();
            assertEquals(Spliterator.SIZED, actual & Spliterator.SIZED);
            assertEquals(Spliterator.SUBSIZED, actual & Spliterator.SUBSIZED);
        }
        {
            var split = AddressSetSpliterator.consume(medium.ranges());
            long actual = split.characteristics();
            assertEquals(0, actual & Spliterator.SIZED);
            assertEquals(0, actual & Spliterator.SUBSIZED);
        }
        {
            var split = AddressSetSpliterator.consume(massive.ranges());
            long actual = split.characteristics();
            assertEquals(0, actual & Spliterator.SIZED);
            assertEquals(0, actual & Spliterator.SUBSIZED);
        }
    }

    @Test
    void getExactSizeIfKnown() {
        {
            int expected = toList(small).size();
            var split = AddressSetSpliterator.consume(small.ranges());
            long actual = split.getExactSizeIfKnown();
            assertEquals(expected, actual);
        }
        {
            var split = AddressSetSpliterator.consume(medium.ranges());
            long actual = split.getExactSizeIfKnown();
            assertEquals(-1, actual);
        }
        {
            var split = AddressSetSpliterator.consume(massive.ranges());
            long actual = split.getExactSizeIfKnown();
            assertEquals(-1, actual);
        }
        {
            var empty = AddressSets.of();
            var nothing = AddressSetSpliterator.consume(empty.ranges());
            assertEquals(0, nothing.getExactSizeIfKnown());
        }
    }

    private <T> List<T> toList(Iterable<T> source) {
        var list = new ArrayList<T>();
        for(var element : source) {
            list.add(element);
        }
        return list;
    }

    @SafeVarargs
    private <T> List<T> toList(Spliterator<T>... spliterators) {
        var list = new ArrayList<T>();
        for (var s : spliterators) {
            while (s.tryAdvance(list::add));
        }
        return list;
    }
}