package uk.ipfreely.sets;

import uk.ipfreely.Address;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

final class RangeSpliterator<A extends Address<A>> implements Spliterator<A> {

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
        if (diff.family().max().equals(diff)) {
            return Long.MAX_VALUE;
        }
        diff = diff.next();
        long high = diff.highBits();
        long low = diff.lowBits();
        return high == 0 && low >= 0
                ? low
                : Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED | Spliterator.NONNULL;
    }

    @Override
    public Comparator<? super A> getComparator() {
        // null because IP implements Comparable
        return null;
    }
}
