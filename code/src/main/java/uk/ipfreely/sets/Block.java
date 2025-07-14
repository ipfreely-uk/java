// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;

import java.math.BigInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static uk.ipfreely.sets.Validation.validate;

/**
 * <p>
 *     {@link Range} interface that forms
 *     <a target="_top" href="https://tools.ietf.org/html/rfc4632">RFC-4632 Classless Inter-domain Routing</a> block of
 *     IP {@link Addr}esses.
 * </p>
 * <p>Example: {@code "192.168.0.0/24"}.</p>
 * <ul>
 * <li>First address: {@code 192.168.0.0}.</li>
 * <li>Last address: {@code 192.168.0.255}.</li>
 * <li>Network mask: {@code 255.255.255.0}.</li>
 * <li>Mask size: {@code 24} bits.</li>
 * <li>Size of block: 256 addresses.</li>
 * </ul>
 * <p>See {@link AddressSet} for implementation contract.</p>
 *
 * @param <A> the type for the IP version
 * @see Family#subnets()
 * @see uk.ipfreely.Subnets
 */
public interface Block<A extends Addr<A>> extends Range<A> {

    /**
     * The block in CIDR notation like {@code 192.168.100.0/24}.
     *
     * @return canonical form that can be parsed
     * @see #maskSize()
     */
    default String cidrNotation() {
        return first().toString() + '/' + maskSize();
    }

    /**
     * Mask size in bits as defined by CIDR notation.
     * For {@code 172.26.0.0/16} this returns {@code 16}.
     *
     * @return mask size in bits
     */
    default int maskSize() {
        A first = first();
        return first.family().subnets().maskBits(first, last());
    }

    /**
     * The CIDR mask as an IP address.
     * For {@code 172.26.0.0/16} this returns {@code 255.255.0.0}.
     *
     * <pre><code>
     *     first OR mask       == first
     *     last AND mask       == first
     *     first OR (NOT mask) == last
     * </code></pre>
     *
     * @return the mask IP
     */
    default A mask() {
        return first().family().subnets().masks().get(maskSize());
    }

    /**
     * Number of {@link Addr}esses in block.
     *
     * @return block size
     */
    default BigInteger size() {
        return first().family().subnets().count(maskSize());
    }

    /**
     * Block is never empty.
     *
     * @return false
     */
    default boolean isEmpty() {
        return false;
    }

    /**
     * Divides the block into smaller subnets of given bit mask size.
     *
     * @param size between {@link #maskSize()} and {@link Family#width()} inclusive
     * @return stream of subnet blocks
     */
    default Stream<Block<A>> subnets(int size) {
        int ms = maskSize();
        if (ms == size) {
            return Stream.of(this);
        }

        A first = first();
        Family<A> family = first.family();
        validate(size >= maskSize(), "Not enough mask bits", size, IllegalArgumentException::new);
        validate(size <= family.width(), "Too many mask bits", size, IllegalArgumentException::new);

        A s = family.subnets().masks().get(size).not();
        return StreamSupport.stream(new SubnetSpliterator<>(first, last(), s), false);
    }
}
