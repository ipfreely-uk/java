package uk.ipfreely.collections;

import uk.ipfreely.Address;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class RangeArrayIterator<A extends Address<A>> implements Iterator<A> {

    private final Range<A>[] ranges;
    private int index;
    private Iterator<A> delegate;

    /**
     * @param ranges must not be empty
     */
    @SafeVarargs
    RangeArrayIterator(Range<A>... ranges) {
        this.ranges = ranges;
        nextIterator();
    }

    private void nextIterator() {
        delegate = ranges[index++].iterator();
    }

    @Override
    public boolean hasNext() {
        while (!delegate.hasNext() && index < ranges.length) {
            nextIterator();
        }
        return delegate.hasNext();
    }

    @Override
    public A next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return delegate.next();
    }
}
