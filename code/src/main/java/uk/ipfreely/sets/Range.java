package uk.ipfreely.sets;

import uk.ipfreely.Address;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.math.BigInteger.ONE;
import static uk.ipfreely.sets.Validation.validate;

/**
 * <p>
 *     {@link AddressSet} that forms contiguous range of one or more {@link Address}es.
 * </p>
 * <p>See {@link AddressSet} for implementation contract.</p>
 *
 * @param <A> address type
 */
public interface Range<A extends Address<A>> extends AddressSet<A> {

    /**
     * Least element in the range.
     *
     * @return first
     */
    A first();

    /**
     * Greatest element in the range.
     *
     * @return last
     */
    A last();

    default Stream<Range<A>> ranges() {
        return Stream.of(this);
    }

    /**
     * The range size as a {@link BigInteger}.
     *
     * @return the number of addresses including first and last
     */
    default BigInteger size() {
        return last().subtract(first()).toBigInteger().add(ONE);
    }

    /**
     * Tests argument is same {@link uk.ipfreely.Family} and more than or equal to
     * {@link #first()} and less than or equal to
     * {@link #last()}.
     *
     * @param address the address to test
     * @return true if this sequence contains the given IP address
     */
    @SuppressWarnings("unchecked")
    default boolean contains(Address<?> address) {
        A first = first();
        if (first.getClass() != address.getClass()) {
            return false;
        }
        A addr = (A) address;
        return first.compareTo(addr) <= 0 && last().compareTo(addr) >= 0;
    }

    /**
     * All addresses in the range.
     *
     * @return from first to last
     */
    @Override
    default Iterator<A> iterator() {
        class IncrementIterator implements Iterator<A> {
            private A current = first();
            private final A end = last();

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public A next() {
                validate(current != null, "Iterator exhausted", end, NoSuchElementException::new);
                final A c = current;
                if (c.equals(end)) {
                    current = null;
                } else {
                    current = current.next();
                }
                return c;
            }
        }

        return new IncrementIterator();
    }

    /**
     * <p>
     *     Default {@link Spliterator#characteristics()}:
     *     {@link Spliterator#IMMUTABLE};
     *     {@link Spliterator#DISTINCT};
     *     {@link Spliterator#ORDERED};
     *     {@link Spliterator#SORTED};
     *     {@link Spliterator#NONNULL}.
     * </p>
     * <p>
     *     {@link Spliterator#trySplit()} is implemented.
     * </p>
     *
     * @return instance
     */
    @Override
    default Spliterator<A> spliterator() {
        return new RangeSpliterator<>(first(), last());
    }

    /**
     * All addresses in the range.
     *
     * @return from first to last
     */
    default Stream<A> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Streams arbitrary range as valid CIDR blocks.
     *
     * @return stream
     */
    default Stream<Block<A>> blocks() {
        if (this instanceof Block) {
            return Stream.of((Block<A>) this);
        }
        return StreamSupport.stream(new BlockSpliterator<>(first(), last()), false);
    }

    /**
     * Tests if this range can create a contiguous range with another.
     * That is, either {@link #intersects(Range)} or {@link #adjacent(Range)} are true.
     *
     * @param r another range
     * @return true if contiguous
     */
    default boolean contiguous(Range<A> r) {
        return intersects(r) || adjacent(r);
    }

    /**
     * Tests if there is any overlap in the address ranges.
     *
     * @param r another range
     * @return true on intersection
     */
    default boolean intersects(Range<A> r) {
        A f0 = first();
        A l0 = last();
        A f1 = r.first();
        A l1 = r.last();
        return contains(f1)
                || contains(l1)
                || r.contains(f0)
                || r.contains(l0);
    }

    /**
     * Tests if the last value in either range is one less than the first value of the other.
     *
     * @param r another range
     * @return true if adjacent
     */
    default boolean adjacent(Range<A> r) {
        A f0 = first();
        A l0 = last();
        A f1 = r.first();
        A l1 = r.last();
        return contains(f1.next())
                || contains(l1.prev())
                || r.contains(f0.next())
                || r.contains(l0.prev());
    }

    /**
     * Combines two ranges into a single range using the least and greatest values from each.
     * The ranges do not have to be contiguous.
     *
     * @param other another range
     * @return new range
     * @see #contiguous(Range) 
     */
    default Range<A> combine(Range<A> other) {
        A first = Compare.least(first(), other.first());
        A last = Compare.greatest(last(), other.last());
        // TODO: efficiency - return supersets
        return AddressSets.range(first, last);
    }
}
