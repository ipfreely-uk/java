// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.math.BigInteger;
import java.util.stream.Stream;

/**
 * <p>
 *     Discrete ordered set interface of zero to {@link Family#max()} {@link Address}es.
 * </p>
 * <p>
 *     {@code AddressSet} is tightly coupled to {@link Range} and {@link Block}.
 *     Implementations MUST be immutable.
 *     Iteration methods MUST produce values from least to greatest.
 * </p>
 * <table border="1">
 *     <caption>AddressSet Contracts</caption>
 *     <tr>
 *         <th>
 *             Interface
 *         </th>
 *         <th>
 *             Must Be Implemented When Set...
 *         </th>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.Block}
 *         </td>
 *         <td>
 *             ...forms a valid
 *             <a href="https://tools.ietf.org/html/rfc4632">RFC4632</a>
 *             CIDR block.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.Range}
 *         </td>
 *         <td>
 *             ...is contiguous range.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.AddressSet}
 *         </td>
 *         <td>
 *             ...is made up of non-{@link Range#adjacent(Range)} {@link Range}s or is the empty set.
 *         </td>
 *     </tr>
 * </table>
 * <p>
 *     Mixing {@link uk.ipfreely.V4} and {@link uk.ipfreely.V6} in the same set is not permitted.
 * </p>
 * <p>
 *     <strong>Design note:</strong>
 *     this type does not extend {@link java.util.Collection}
 *     types because {@link #size()} can
 *     exceed {@link java.lang.Integer#MAX_VALUE}.
 * </p>
 * @see AddressSets#from(Iterable)
 * @see AddressSets#of(AddressSet[])
 * @param <A> address type
 */
public interface AddressSet<A extends Address<A>> extends Iterable<A> {

    /**
     * <p>
     *     {@link Range} Contract: other is {@link Range} instance and {@link Range#first()} &amp; {@link Range#last()}
     *     are equal.
     * </p>
     * <p>
     *     General {@code AddressSet} Contract:
     *     returns true if instance of {@code AddressSet} and all constituent
     *     {@link #ranges()} are identical.
     * </p>
     *
     * @param other other
     * @return true if equal
     */
    boolean equals(Object other);

    /**
     * <p>{@link Range} Contract: {@code first().hashCode() * 31 + last().hashCode()}</p>
     * <p>General {@code AddressSet} Contract: {@code ranges().mapToInt(Object::hashCode).reduce(0, (n, r) -> n * 31 + r)}</p>
     *
     * @return size hash
     */
    int hashCode();

    /**
     * <p>
     *     Constituent {@link Range}s.
     * </p>
     * <p>
     *     Where {@link Range#adjacent(Range)} or {@link Range#intersects(Range)} are true ranges MUST be combined into a single {@link Range}.
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
     * Tests if {@link Range#contains(Address)} is true for any of the constituent {@link #ranges()}.
     *
     * @param address candidate
     * @return true if the given address is present
     */
    default boolean contains(Address<?> address) {
        return ranges().anyMatch(r -> r.contains(address));
    }

    /**
     * Number of unique {@link Address}es.
     * The cardinality of the set.
     *
     * @return count
     */
    default BigInteger size() {
        return ranges().map(Range::size).reduce(BigInteger.ZERO, BigInteger::add);
    }
}
