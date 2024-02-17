package uk.ipfreely.examples;

import uk.ipfreely.Address;
import uk.ipfreely.collections.Range;

import java.math.BigInteger;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public final class IpCollections {

    private static final BigInteger LIMIT = BigInteger.valueOf(Integer.MAX_VALUE);

    private IpCollections() {}

    public static <A extends Address<A>> Set<A> asSet(Range<A> range) {
        final BigInteger size = range.size();
        if (LIMIT.compareTo(range.size()) < 0) {
            throw new IllegalArgumentException("Range too large");
        }

        // TODO: NavigableSet

        class RangeSet extends AbstractSet<A> {
            private final int s = size.intValue();

            @Override
            public Iterator<A> iterator() {
                return range.iterator();
            }

            @Override
            public int size() {
                return s;
            }
        }

        return new RangeSet();
    }
}
