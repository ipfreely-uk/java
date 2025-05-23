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

    SubnetSpliterator(A first, A last, A increment) {
        this.current = first;
        this.last = last;
        this.increment = increment;
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
        // TODO
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE | DISTINCT | ORDERED | NONNULL;
    }
}
