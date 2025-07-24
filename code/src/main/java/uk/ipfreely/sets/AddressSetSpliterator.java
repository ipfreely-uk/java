// Copyright 2025 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

final class AddressSetSpliterator<A extends Addr<A>> implements Spliterator<A> {
    private final List<Spliterator<A>> splits;

    private AddressSetSpliterator(List<Spliterator<A>> mutable) {
        // retain and own list
        this.splits = mutable;
    }

    static <A extends Addr<A>> AddressSetSpliterator<A> consume(Stream<Range<A>> ranges) {
        var spliterators = ranges.map(Range::spliterator)
                .collect(toCollection(ArrayList::new));
        return new AddressSetSpliterator<>(spliterators);
    }

    @Override
    public boolean tryAdvance(Consumer<? super A> action) {
        for (var it = splits.iterator(); it.hasNext();) {
            if(it.next().tryAdvance(action)) {
                return true;
            }
            it.remove();
        }
        return false;
    }

    @Override
    public Spliterator<A> trySplit() {
        int size = splits.size();
        int half = size / 2;
        if (half < 1) {
            return null;
        }
        var head = new ArrayList<Spliterator<A>>(half);
        for (int i = 0; i < half; i++) {
            var s = splits.remove(0);
            head.add(s);
        }
        return new AddressSetSpliterator<>(head);
    }

    @Override
    public long estimateSize() {
        long estimate = 0L;
        for (var s : splits) {
            long rangeSize = s.estimateSize();
            if (rangeSize == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            estimate += rangeSize;
            if (estimate < 0L) {
                return Long.MAX_VALUE;
            }
        }
        return estimate;
    }

    @Override
    public int characteristics() {
        int chrctrstcs = IMMUTABLE | ORDERED | SORTED | NONNULL | DISTINCT;
        if (estimateSize() < Long.MAX_VALUE) {
            // when estimate is less than MAX_VALUE can report exact size
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
}
