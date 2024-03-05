package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

/**
 * Provides utility methods for working with
 * <a href="https://tools.ietf.org/html/rfc4632">RFC-4632 Classless Inter-domain Routing</a>
 * blocks of IP addresses for a given {@link Family}.
 * Obtain instances from {@link Family#subnets()}.
 *
 * @param <A> address type
 */
public final class Subnets<A extends Address<A>> {
    private final Family<A> family;

    Subnets(Family<A> family) {
        this.family = family;
    }

    /**
     * IP address family.
     *
     * @return {@link Family#v4()} or {@link Family#v6()}
     */
    public Family<A> family() {
        return family;
    }

    /**
     * <p>
     * All possible IP address masks for this family.
     * The masks are indexed by mask size.
     * </p>
     * <p>
     * For IPv4 index 0 is <em>/0</em> {@code "0.0.0.0"}, index 1 is <em>/1</em> {@code "128.0.0.0"},
     * and index 32 is <em>/32</em> {@code "255.255.255.255"}.
     * </p>
     * <p>
     * For IPv6 index 0 is <em>/0</em> {@code "::"}
     * and index 128 is <em>/128</em> {@code "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"}.
     * </p>
     *
     * @return all possible network address masks
     */
    public List<A> masks() {
        return family.masks();
    }

    /**
     * <p>
     *     Calculates the mask bit size for an IP address CIDR block range.
     *     The first argument MUST be less than or equal to the last.
     * </p>
     * <p>
     *     For {@code "172.0.0.0"} and {@code "172.255.255.255"} forming block {@code "172.0.0.0/8"} this will return {@code 8}.
     * </p>
     * <p>
     *     The return value can be used as the mask index for {@link #masks()}.
     *     Valid mask sizes are from zero to {@link Family#width()} inclusive.
     * </p>
     * <p>
     *     {@code -1} is returned if the arguments do not form a valid block.
     * </p>
     * <pre><code>
     *     // EXAMPLE
     *     V4 first = Family.v4().parse("127.0.0.0");
     *     V4 last = Family.v4().parse("127.255.255.255");
     *     Subnets&lt;V4&gt; subnets = Family.v4().subnets();
     *     // 8
     *     int maskBits = subnets.maskBits(first, last);
     *     // 255.0.0.0
     *     V4 mask = subnets.masks().get(maskBits);
     * </code></pre>
     *
     * @param first the first element in an IP range
     * @param last  the last element in an IP range
     * @return mask size in bits or -1 if arguments not a valid CIDR block
     */
    public int maskBits(A first, A last) {
        return family.maskBitsForBlock(first, last);
    }

    /**
     * The number of addresses for the number of bits in a CIDR notation mask.
     * Use {@code maskAddressCount(0)} to get the number of IP addresses in this family.
     * <pre><code>
     *     // EXAMPLE
     *     int maskBits = 22;
     *     V4 network = Family.v4().parse("10.9.0.0");
     *     V4 mask = Family.v4().subnets().masks().get(maskBits);
     *     BigInteger addresses = Family.v4().subnets().count(maskBits);
     *     // "10.9.0.0/22 has 1024 addresses and mask 255.255.252.0"
     *     String description = network + "/" + maskBits + " has " + addresses + " addresses and mask " + mask;
     * </code></pre>
     *
     * @param maskBits between 0 and the family width in bits (inclusive)
     * @return the count of addresses for a given subnet size
     * @see Family#width()
     */
    public BigInteger count(int maskBits) {
        return family.maskAddressCount(maskBits);
    }

    /**
     * Informational.
     *
     * @return IP family version
     */
    @Override
    public String toString() {
        return family.toString();
    }
}
