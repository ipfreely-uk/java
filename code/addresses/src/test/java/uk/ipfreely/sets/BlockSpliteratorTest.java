// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.testing.SpliteratorTester;

import java.math.BigInteger;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BlockSpliteratorTest {

    @Test
    void tryAdvance() {
        {
            Range<V4> subnet = AddressSets.parseCidr(Family.v4(), "192.168.0.0/24");
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
            Range<V6> big = AddressSets.parseCidr(Family.v6(), "dead::/16");
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
        {
            Range<V6> big = AddressSets.parseCidr(Family.v6(), "fe80::/64");
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
        {
            V6 one = Family.v6().parse(1);
            V6 two = Family.v6().parse(2);
            AtomicReference<Block<V6>> actual = new AtomicReference<>();

            BlockSpliterator<V6> sb = new BlockSpliterator<>(one, two);
            boolean acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(AddressSets.address(one), actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(AddressSets.address(two), actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertFalse(acquired);
        }
        {
            V4 one = Family.v4().parse(1);
            V4 two = Family.v4().parse(2);
            AtomicReference<Block<V4>> actual = new AtomicReference<>();

            BlockSpliterator<V4> sb = new BlockSpliterator<>(one, two);
            boolean acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(AddressSets.address(one), actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertTrue(acquired);
            assertEquals(AddressSets.address(two), actual.get());

            acquired = sb.tryAdvance(actual::set);
            assertFalse(acquired);
        }
    }

    @Test
    void trySplit() {
        {
            Range<V4> subnet = AddressSets.parseCidr(Family.v4(), "192.168.0.0/24");

            BlockSpliterator<V4> sb = new BlockSpliterator<>(subnet.first(), subnet.last().next());
            Spliterator<Block<V4>> split = sb.trySplit();

            assertNull(split);
        }
        {
            Range<V6> subnet = AddressSets.parseCidr(Family.v6(), "fe80::/24");

            BlockSpliterator<V6> sb = new BlockSpliterator<>(subnet.first(), subnet.last().next());
            Spliterator<Block<V6>> split = sb.trySplit();

            assertNull(split);
        }
    }

    @Test
    void spliterator() {
        Range<V6> range = AddressSets.block(Family.v6().parse(1, 0), 64);
        Spliterator<Block<V6>> s = new BlockSpliterator<V6>(range.first(), range.last());
        SpliteratorTester.test(s);
    }
}