package uk.ipfreely.collections;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.math.BigInteger.ONE;
import static uk.ipfreely.collections.Validation.validate;

/**
 * <p>
 *     Contiguous range of one or more IP addresses. Ranges are ordered from least to greatest.
 * </p>
 * <p>
 *     {@link Range} is tightly coupled to the {@link Block} subtype.
 *     Any implementation that is a valid CIDR block MUST implement {@link Block}.
 *     Implementations MUST be immutable.
 * </p>
 * <p>
 *     Care should be taken iterating over large ranges.
 *     The IPv6 address block <code>::/0</code> is effectively infinite for the purposes of iteration.
 * </p>
 * <p>
 *     A {@link Range} that is not a {@link Block} is useful for representing, for example,
 *     the set of assignable addresses in an IPv4 subnet - everything except first and last.
 * </p>
 *
 * @param <A> address type
 */
public interface Range<A extends Address<A>> extends AddressCollection<A> {

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

    /**
     * Contract: {@link Range} instances are equal if their first and last values are equal.
     *
     * @param other object or null
     * @return true if argument is a Range and first and last are equal
     */
    boolean equals(Object other);

    /**
     * Contract: {@code first().hashCode() * 31 + last().hashCode()}.
     *
     * @return the prescribed hashCode implementation
     */
    int hashCode();

    /**
     * IP address family.
     *
     * @return {@link Family#v4()} or {@link Family#v6()}
     */
    default Family<A> family() {
        return first().family();
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
     * Tests address intersection.
     *
     * @param other the other sequence
     * @return true if these ranges overlap
     */
    default boolean intersects(Range<A> other) {
        return contains(other.first()) || other.contains(first());
    }

    /**
     * The intersection of two ranges.
     *
     * @param other another range
     * @return common elements
     */
    default Optional<Range<A>> intersection(Range<A> other) {
        final Range<A> common;

        if (other.contains(first())) {
            common = other.contains(last())
                    ? this
                    : Ranges.from(first(), other.last());
        } else if (other.contains(last())) {
            common = Ranges.from(other.first(), last());
        } else if (this.contains(other.first())) {
            common = this.contains(other.last())
                    ? other
                    : Ranges.from(other.last(), first());
        } else {
            return Optional.empty();
        }

        return Optional.of(common);
    }

    /**
     * Tests the adjacency of two ranges.
     *
     * @param other another block
     * @return true if two ranges may be combined into one
     */
    default boolean adjacent(Range<A> other) {
        return other.first().prev().equals(first())
                || other.last().next().equals(last());
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
     * Convenience method for obtaining {@link Block}.
     *
     * @return as block if valid
     */
    default Optional<Block<A>> asBlock() {
        return this instanceof Block
                ? Optional.of((Block<A>) this)
                : Optional.empty();
    }

    /**
     * Streams arbitrary range as valid CIDR blocks.
     *
     * @return stream
     */
    default Stream<Block<A>> blocks() {
        return asBlock().map(Stream::of)
                .orElseGet(() -> StreamSupport.stream(new BlockSpliterator<>(first(), last()), false));
    }
}
