// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Address;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static uk.ipfreely.sets.Validation.validate;

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
        validate(hasNext(), "Iterator exhausted", null, NoSuchElementException::new);
        return delegate.next();
    }
}
