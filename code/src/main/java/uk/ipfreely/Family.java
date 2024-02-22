package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

/**
 * <p>
 *  Two instances can be obtained via {@link #v4()} and {@link #v6()}.
 *  These represent <a href="https://www.rfc-editor.org/rfc/rfc791">IpV4</a>
 *  and <a href="https://www.rfc-editor.org/rfc/rfc2460">IpV6</a>.
 * </p>
 * <p>
 *     Inheritance outside the package is not supported.
 *     Instances have identity equality.
 *     Future implementations may become
 *     <a href="https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html">sealed</a>.
 * </p>
 *
 * @param <A> {@link V4} or {@link V6}
 */
public abstract class Family<A extends Address<A>> {
    Family() {}

    /**
     * IPv4.
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
     * @param address valid IP address in this family
     * @return parsed address
     * @throws ParseException on invalid address
     */
    public abstract A parse(CharSequence address);

    /**
     * Argument must be {@code bitWidth() / 8} bytes in length.
     *
     * @param address the address as bytes
     * @return parsed address
     * @throws ParseException if the array is not the expected length
     */
    public abstract A parse(byte... address);

    /**
     * Enables the conversion from {@link BigInteger} to the {@link Address} type.
     * The largest acceptable value is {@code max().toBigInteger()}.
     *
     * @param address must be between zero and the maximum value inclusive
     * @return the new address
     * @throws ParseException when the argument is out of range
     * @see Address#toBigInteger()
     */
    public abstract A parse(BigInteger address);

    /**
     * Convenience method for simple arithmetic operations.
     * All values of <code>int</code> are valid for this method.
     *
     * @param low the low bits treated as unsigned integer
     * @return IP address
     */
    public abstract A fromUint(int low);

    /**
     * Width of the IP address type in bits.
     *
     * @return 32 (V4) or 128 (V6)
     */
    public abstract int bitWidth();

    /**
     * IP address type.
     *
     * @return either the V4 or V6 IP address class
     */
    public abstract Class<A> ipType();

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
    public abstract List<A> masks();

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
     * Calculates the mask bit size for a given IP address block.
     * For {@code "172.0.0.0"} and {@code "172.255.255.255"} forming block {@code "172.0.0.0/8"} this will return {@code 8}.
     * The first element MUST be less than or equal to the last.
     * This method can be used to detect if an IP range is a valid block.
     * The return value can be used as the mask index for {@link #masks()}.
     * The maximum return value is {@link #bitWidth()}.
     *
     * @param first the first element in an IP range
     * @param last  the last element in an IP range
     * @return the mask size or -1 if this is not a valid block range
     */
    public abstract int maskBitsForBlock(A first, A last);

    /**
     * The number of addresses for the number of bits in a CIDR notation mask.
     * Use {@code maskAddressCount(0)} to get the number of IP addresses in this family.
     *
     * @param maskBits between 0 and the family width in bits (inclusive)
     * @return the count of addresses for a given subnet size
     * @see #bitWidth()
     */
    public abstract BigInteger maskAddressCount(int maskBits);

    /**
     * Regular expression for detecting IP addresses.
     *
     * @return regular expression for matching address patterns
     * @see java.util.regex.Pattern
     */
    public abstract String regex();

    /**
     * Use when IP address family is not known.
     *
     * @param candidate IP address
     * @return instance of {@link V4} or {@link V6}
     * @throws ParseException on invalid address
     * @see #parse(CharSequence)
     */
    public static Address<?> parseUnknown(CharSequence candidate) {
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
            break;
        }
        throw new ParseException("Not IP address:" + candidate);
    }

    /**
     * If the family is known use {@link Family#parse(byte...)} instead.
     *
     * @param address an IPv4 or IPv6 address in byte form
     * @return parsed address
     * @throws ParseException if array is not 4 (V4) or 16 (V6) bytes in length
     */
    public static Address<?> parseUnknown(byte... address) {
        int v4len = V4Consts.WIDTH / Byte.SIZE;
        int v6len = V6Consts.WIDTH / Byte.SIZE;
        boolean v4 = (v4len == address.length);
        boolean v6 = (v6len == address.length);
        validate(v4 || v6, "IP addresses must be " + v4len + " or " + v6len + " bytes in length", address, ParseException::new);

        return v4 ? V4Family.INST.parse(address) : V6Family.INST.parse(address);
    }
}
