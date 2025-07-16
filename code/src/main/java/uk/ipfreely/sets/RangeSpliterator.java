// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

final class RangeSpliterator<A extends Addr<A>> implements Spliterator<A> {

    private A current;
    private final A last;

    RangeSpliterator(A current, A last) {
        this.current = current;
        this.last = last;
    }

    @Override
    public boolean tryAdvance(Consumer<? super A> action) {
        if (current == null) {
            return false;
        }
        A r = current;
        current = r.equals(last) ? null : current.next();
        action.accept(r);
        return true;
    }

    @Override
    public Spliterator<A> trySplit() {
        if (current == null || current.equals(last)) {
            return null;
        }
        A two = current.family().parse(2);
        A size = last.subtract(current);
        A mid = size.divide(two).add(current);
        // must return prefix because ORDERED
        RangeSpliterator<A> prefix = new RangeSpliterator<>(current, mid);
        current = mid.next();
        return prefix;
    }

    @Override
    public long estimateSize() {
        A diff = last.subtract(current);
        long high = diff.highBits();
        long low = diff.lowBits();
        return (high == 0
                && low >= 0
                && low < Long.MAX_VALUE)
                ? low + 1
                : Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        int chrctrstcs = IMMUTABLE | DISTINCT | SORTED | ORDERED | NONNULL;
        if (estimateSize() < Long.MAX_VALUE) {
            // when estimate is less than MAX_VALUE can report exact size via
            chrctrstcs |= SIZED | SUBSIZED;
        }
        return chrctrstcs;
    }

    @Override
    public long getExactSizeIfKnown() {
        long estimate = estimateSize();
        return estimate < Long.MAX_VALUE
                ? estimate
                : -1;
    }

    @Override
    public Comparator<? super A> getComparator() {
        // null because A implements Comparable & everything SORTED in natural order
        return null;
    }
}
