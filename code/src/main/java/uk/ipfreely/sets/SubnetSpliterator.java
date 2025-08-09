// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.Spliterator;
import java.util.function.Consumer;

final class SubnetSpliterator<A extends Addr<A>> implements Spliterator<Block<A>> {

    private A current;
    private final A last;
    private final A increment;

    SubnetSpliterator(A first, A last, int bitSize) {
        this.current = first;
        this.last = last;
        var family = first.family();
        // how much to add to get end of current Block
        this.increment = family.subnets().masks().get(bitSize).not();
    }

    @Override
    public boolean tryAdvance(Consumer<? super Block<A>> action) {
        if (current == null) {
            return false;
        }
        A end = current.add(increment);
        Block<A> b = AddressSets.block(current, end);
        action.accept(b);
        current = end.equals(last) ? null : end.next();
        return true;
    }

    @Override
    public Spliterator<Block<A>> trySplit() {
        // TODO
        return null;
    }

    @Override
    public long estimateSize() {
        if (current == null) {
            return 0;
        }
        A blockSize = increment.next();
        A remaining = last.subtract(current);
        A size = remaining.divide(blockSize);
        long high = size.highBits();
        long low = size.lowBits();
        return (high == 0) && (low >= 0) && (low < Long.MAX_VALUE)
                ? low + 1
                : Long.MAX_VALUE;
    }

    @Override
    public long getExactSizeIfKnown() {
        long estimate = estimateSize();
        return (estimate == Long.MAX_VALUE)
                ? -1
                : estimate;
    }

    @Override
    public int characteristics() {
        int ch = IMMUTABLE | DISTINCT | ORDERED | NONNULL;
        if (estimateSize() != Long.MAX_VALUE) {
            ch |= SIZED | SUBSIZED;
        }
        return ch;
    }
}
