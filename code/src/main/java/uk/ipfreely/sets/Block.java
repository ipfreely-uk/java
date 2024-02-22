package uk.ipfreely.sets;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.math.BigInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static uk.ipfreely.sets.Validation.validate;

/**
 * <p>
 *     <a href="https://tools.ietf.org/html/rfc4632">RFC-4632 Classless Inter-domain Routing</a> block of IP addresses.
 * </p>
 * <p>Example: {@code "192.168.0.0/24"}.</p>
 * <ul>
 * <li>First address: {@code 192.168.0.0}.</li>
 * <li>Last address: {@code 192.168.0.255}.</li>
 * <li>Network mask: {@code 255.255.255.0}.</li>
 * <li>Mask size: {@code 24} bits.</li>
 * <li>Size of block: 256 addresses.</li>
 * </ul>
 *
 * @param <A> the type for the IP version
 */
public interface Block<A extends Address<A>> extends Range<A> {

    /**
     * The block in CIDR notation like {@code 192.168.100.0/24}.
     * See <a href="https://tools.ietf.org/html/rfc4632">RFC4632</a>.
     *
     * @return canonical form that can be parsed
     * @see #maskBits()
     */
    default String cidrNotation() {
        return first().toString() + '/' + maskBits();
    }

    /**
     * The mask size of the block as defined by CIDR notation.
     * For {@code 172.26.0.0/16} this returns {@code 16}.
     *
     * @return mask size in bits
     */
    default int maskBits() {
        A first = first();
        return first.family().maskBitsForBlock(first, last());
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
        return first().family().masks().get(maskBits());
    }

    /**
     * Size of the CIDR block.
     *
     * @return block size
     */
    default BigInteger size() {
        return first().family().maskAddressCount(maskBits());
    }

    /**
     * Divides the block into CIDR subnets.
     *
     * @param maskBits CIDR mask size of the subnets
     * @return stream of subnet blocks
     */
    default Stream<Block<A>> subnets(int maskBits) {
        A first = first();
        Family<A> family = first.family();
        validate(maskBits >= maskBits(), "Not enough mask bits", maskBits, IllegalArgumentException::new);
        validate(maskBits <= family.bitWidth(), "Too many mask bits", maskBits, IllegalArgumentException::new);

        A size = family.masks().get(maskBits).not();
        return StreamSupport.stream(new SubnetSpliterator<>(first, last(), size), false);
    }
}
