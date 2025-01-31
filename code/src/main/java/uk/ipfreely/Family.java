// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

/**
 * <p>
 *  {@link Addr} factory and utility type for
 *  <a href="https://www.rfc-editor.org/rfc/rfc791">IpV4</a>
 *  and
 *  <a href="https://www.rfc-editor.org/rfc/rfc2460">IpV6</a>
 *  obtained via {@link #v4()} and {@link #v6()}.
 * </p>
 * <p>
 *     There are only two instances of this type.
 *     Inheritance outside the package is not supported.
 *     Instances have identity equality.
 *     Future implementations may become
 *     <a href="https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html">sealed</a>.
 * </p>
 *
 * @param <A> {@link V4} or {@link V6}
 */
public abstract class Family<A extends Addr<A>> {
    private final Subnets<A> subnets = new Subnets<>(this);

    Family() {}

    /**
     * IPv4.
     *
     * <pre><code>
     *     // EXAMPLE
     *     V4 localhost = Family.v4().parse("127.0.0.1");
     * </code></pre>
     *
     * @return IPv4 family of addresses
     */
    public static Family<V4> v4() {
        return V4Family.INST;
    }

    /**
     * IPv6.
     *
     * @return IPv6 family of addresses
     */
    public static Family<V6> v6() {
        return V6Family.INST;
    }

    /**
     * Informational.
     *
     * @return family as string
     */
    @Override
    public abstract String toString();

    /**
     * <p>
     *     IP address instantiation from primitives.
     * </p>
     * <p>
     *     Only values up to <code>(0L, 0xffffffffL)</code> are valid for IPv4.
     * </p>
     * <p>
     *     All values are valid for IPv6.
     * </p>
     *
     * @param high high bits of the IP address
     * @param low  low bits of the IP address
     * @return the address
     * @throws ParseException on invalid values
     */
    public abstract A parse(long high, long low);

    /**
     * Parses an IP address string.
     * TODO: supported string forms.
     *
     * @param candidate valid IP address in this family
     * @return parsed address
     * @throws ParseException on invalid address
     */
    public abstract A parse(CharSequence candidate);

    /**
     * Argument must be {@code bitWidth() / 8} bytes in length.
     *
     * @param address the address as bytes
     * @return parsed address
     * @throws ParseException if the array is not the expected length
     */
    public abstract A parse(byte... address);

    /**
     * Enables the conversion from {@link BigInteger} to the {@link Addr} type.
     * The largest acceptable value is {@code max().toBigInteger()}.
     *
     * @param address must be between zero and the maximum value inclusive
     * @return the new address
     * @throws ParseException when the argument is out of range
     * @see Addr#toBigInteger()
     */
    public abstract A parse(BigInteger address);

    /**
     * Convenience method for creating {@link Addr} from number.
     * All values of <code>int</code> are valid for this method.
     * Every value in the IPv4 range can be created with this method.
     *
     * @param unsigned integer treated as unsigned value
     * @return IP address
     */
    public abstract A parse(int unsigned);

    /**
     * Width of the IP address type in bits.
     *
     * @return 32 (V4) or 128 (V6)
     */
    public abstract int width();

    /**
     * IP address type.
     *
     * @return {@link V4}.class or {@link V6}.class
     */
    public abstract Class<A> type();

    /**
     * Subnet utilities.
     *
     * @return subnet methods for this family
     */
    public Subnets<A> subnets() {
        return subnets;
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
    abstract List<A> masks();

    /**
     * Zero.
     *
     * @return "0.0.0.0" (V4) or "::" (V6)
     */
    public A min() {
        return masks().get(0);
    }

    /**
     * Maximum IP value.
     *
     * @return "255.255.255.255" (V4) or "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff" (V6)
     */
    public A max() {
        final List<A> m = masks();
        return m.get(m.size() - 1);
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
     *     Valid mask sizes are from zero to {@link #width()} inclusive.
     * </p>
     * <p>
     *     {@code -1} is returned if the arguments do not form a valid block.
     * </p>
     * <pre><code>
     *     // EXAMPLE
     *     V4 first = Family.v4().parse("127.0.0.0");
     *     V4 last = Family.v4().parse("127.255.255.255");
     *     // 8
     *     int maskBits = Family.v4().maskBitsForBlock(first, last);
     *     // 255.0.0.0
     *     V4 mask = Family.v4().masks().get(maskBits);
     * </code></pre>
     *
     * @param first the first element in an IP range
     * @param last  the last element in an IP range
     * @return mask size in bits or -1 if this is not a valid CIDR block range
     */
    abstract int maskBitsForBlock(A first, A last);

    /**
     * The number of addresses for the number of bits in a CIDR notation mask.
     * Use {@code maskAddressCount(0)} to get the number of IP addresses in this family.
     *
     * @param maskBits between 0 and the family width in bits (inclusive)
     * @return the count of addresses for a given subnet size
     * @see #width()
     */
    abstract BigInteger maskAddressCount(int maskBits);

    /**
     * <p>Regular expression for detecting IP addresses in this family.</p>
     * <pre><code>
     *     // EXAMPLE
     *     String startOfString = "^";
     *     String endOfString = "$";
     *     String or = "|";
     *     String v4r = Family.v4().regex();
     *     String v6r = Family.v6().regex();
     *     Pattern addressPattern = Pattern.compile(startOfString + v4r + or + v6r + endOfString);
     *
     *     for (String candidate : new String[]{"172.0.0.1", "foo", "::1",}) {
     *         Matcher m = addressPattern.matcher(candidate);
     *         if (m.matches()) {
     *             System.out.println(Family.unknown(candidate).family() + "\t" + candidate);
     *         } else {
     *             System.out.println("none\t" + candidate);
     *         }
     *     }
     * </code></pre>
     *
     * @return regular expression for matching address patterns
     * @see java.util.regex.Pattern
     */
    public abstract String regex();

    /**
     * Uses heuristics to detect IP address family and calls {@link #parse(CharSequence)}.
     *
     * @param candidate IP address
     * @return instance of {@link V4} or {@link V6}
     * @throws ParseException on invalid address
     * @see Addr#toString()
     */
    public static Addr<?> unknown(CharSequence candidate) {
        return detect(candidate)
                .parse(candidate);
    }

    private static Family<?> detect(CharSequence candidate) {
        for (int i = 0, len = candidate.length(); i < len; i++) {
            char ch = candidate.charAt(i);
            if (ch >= '0' && ch <= '9') {
                continue;
            }
            if (ch == '.') {
                return V4Family.INST;
            }
            if (ch == ':') {
                return V6Family.INST;
            }
            if (ch >= 'a' && ch <= 'f') {
                return V6Family.INST;
            }
            if (ch >= 'A' && ch <= 'F') {
                return V6Family.INST;
            }
        }
        throw new ParseException("Not IP address:" + candidate);
    }

    /**
     * Uses array length to detect IP address family and calls {@link #parse(byte...)}.
     *
     * @param address an IPv4 or IPv6 address in byte form
     * @return parsed address
     * @throws ParseException if array is not 4 (V4) or 16 (V6) bytes in length
     * @see Addr#toBytes()
     */
    public static Addr<?> unknown(byte... address) {
        int v4len = Consts.V4_WIDTH / Byte.SIZE;
        int v6len = Consts.V6_WIDTH / Byte.SIZE;
        boolean v4 = v4len == address.length;
        boolean v6 = v6len == address.length;
        validate(v4 || v6, "IP addresses must be " + v4len + " or " + v6len + " bytes in length", address, ParseException::new);

        return v4 ? V4Family.INST.parse(address) : V6Family.INST.parse(address);
    }
}
