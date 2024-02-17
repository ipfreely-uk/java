package uk.ipfreely.collections;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * <p>
 *     Set of zero to {@link Family#max()} {@link Address}es.
 * </p>
 * <p>
 *     Intended for small sets of {@link Range}s like the set of private IP address ranges.
 * </p>
 *
 * @param <A> address type
 */
public interface AddressSet<A extends Address<A>> extends AddressCollection<A> {

    /**
     * Contract: returns true if instance of {@code AddressSet} and all constituent ranges are identical.
     *
     * @param o other
     * @return true if equal
     */
    boolean equals(Object o);

    /**
     * Contract: returns {@code size().hashCode()}.
     *
     * @return size hash
     */
    int hashCode();

    /**
     * <p>
     *     Constituent {@link Range}s.
     * </p>
     * <p>
     *     Adjacent or overlapping ranges MUST be combined into a single {@link Range}.
     *     Elements MUST be produced from least {@link Address} to greatest.
     * </p>
     * <p>
     *     A {@link Stream} of {@link Address} values can be obtained with {@code ranges().flatMap(Range::stream)}.
     *     A {@link Stream} of {@link Block}s can be obtained with {@code ranges().flatMap(Range::blocks)}.
     * </p>
     *
     * @return constituent ranges
     */
    Stream<Range<A>> ranges();

    /**
     * Checks for {@link Address} membership.
     *
     * @param address candidate
     * @return true if the given address is present
     */
    default boolean contains(Address<?> address) {
        return ranges().anyMatch(r -> r.contains(address));
    }

    /**
     * Number of unique {@link Address}es in this set.
     *
     * @return count
     */
    default BigInteger size() {
        return ranges().map(Range::size).reduce(BigInteger.ZERO, BigInteger::add);
    }
}
