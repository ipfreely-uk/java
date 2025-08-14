// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.V4;
import uk.ipfreely.testing.SpliteratorTester;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

class SubnetSpliteratorTest {

    @Test
    void spliterator() {
        V4 zero = v4().min();
        V4 seven = v4().parse(7);
        var s = new SubnetSpliterator<>(zero, seven, v4().width());
        SpliteratorTester.test(s);
    }

    @Test
    void reportsSized() {
        V4 zero = v4().min();
        V4 seven = v4().parse(7);
        var s = new SubnetSpliterator<>(zero, seven, v4().width());
        assertEquals(Spliterator.SIZED, s.characteristics() & Spliterator.SIZED);
        assertEquals(8L, s.estimateSize());
        assertEquals(8L, s.getExactSizeIfKnown());
        AtomicLong count = new AtomicLong();
        while(s.tryAdvance(a -> count.incrementAndGet()));
        assertEquals(8L, count.get());
        assertEquals(0L, s.estimateSize());
        assertEquals(0L, s.getExactSizeIfKnown());
        assertNull(s.trySplit());
    }

    @Test
    void reportsUnsized() {
        var s = new SubnetSpliterator<>(v6().min(), v6().max(), v6().width());
        assertEquals(0L, s.characteristics() & Spliterator.SIZED);
        assertEquals(Long.MAX_VALUE, s.estimateSize());
        assertEquals(-1L, s.getExactSizeIfKnown());
    }
}
