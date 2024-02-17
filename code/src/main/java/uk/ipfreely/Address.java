package uk.ipfreely;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 * <p>
 *     An IP address value type.
 *     Use {@link Family} to create instances.
 * </p>
 * <p>
 *     Implementations are immutable positive integers that support arithmetic and bitwise ops.
 *     Mixing V4 and V6 types is NOT supported.
 * </p>
 *
 * <h2>Usage Hints</h2>
 *
 * <table border="1">
 *      <caption>Comparison with {@link java.net.InetAddress}</caption>
 *      <tr>
 *          <th>Feature</th>
 *          <th>{@link Address}</th>
 *          <th>{@link java.net.InetAddress}</th>
 *      </tr>
 *      <tr>
 *          <td>IP address</td>
 *          <td>YES</td>
 *          <td>YES</td>
 *      </tr>
 *      <tr>
 *          <td>Network I/O</td>
 *          <td>NO</td>
 *          <td>YES</td>
 *      </tr>
 *      <tr>
 *          <td>DNS host names</td>
 *          <td>no</td>
 *          <td>YES</td>
 *      </tr>
 *      <tr>
 *          <td>Numeric type</td>
 *          <td>YES</td>
 *          <td>no</td>
 *      </tr>
 *      <tr>
 *          <td>Generic type</td>
 *          <td>YES</td>
 *          <td>no</td>
 *      </tr>
 *      <tr>
 *          <td>Canonical string form (RFC5952)</td>
 *          <td>YES</td>
 *          <td>no</td>
 *      </tr>
 *      <tr>
 *          <td>{@link Comparable}</td>
 *          <td>YES</td>
 *          <td>no</td>
 *      </tr>
 *      <tr>
 *          <td>{@link java.io.Serializable}</td>
 *          <td>no</td>
 *          <td>yes</td>
 *      </tr>
 *  </table>
 *
 * <p>
 *     For {@link java.net.InetAddress} see also:
 *     <a href="https://commons.apache.org/proper/commons-net/">Apache Commons Net</a>;
 *     <a href="https://guava.dev/">Google Guava</a>.
 * </p>
 *
 * <p>
 *     Inheritance outside the package is not supported.
 * </p>
 *
 * @param <A> the address type
 */
public abstract class Address<A extends Address<A>> implements Comparable<A> {

    Address() {}

    /**
     * Internet protocol family - V4 or V6.
     *
     * @return address family
     */
    public abstract Family<A> family();

    /**
     * Object equality.
     *
     * @param other another object or null
     * @return true if same family and value
     */
    @Override
    public abstract boolean equals(Object other);

    /**
     * Hash code.
     *
     * @return object hash
     */
    @Override
    public abstract int hashCode();

    /**
     * <p>String form amenable to parsers.</p>
     *
     * @return canonical form
     */
    public abstract String toString();

    /**
     * <p>
     *     Convenience method for producing the address in <a href="https://www.rfc-editor.org/rfc/rfc4632">CIDR</a>
     *     notation. "127.0.0.1" becomes "127.0.0.1/32" where the trailing integer is {@link Family#bitWidth()}.
     * </p>
     *
     * @return CIDR notation
     */
    public String cidrNotation() {
        return toString() + '/' + family().bitWidth();
    }

    /**
     * The address as a {@link BigInteger}.
     *
     * @return the address as a positive integer
     */
    public abstract BigInteger toBigInteger();

    /**
     * The address as bytes of length {@link Family#bitWidth()} / {@link Byte#SIZE}.
     *
     * @return the address as a byte sequence, most significant bits first
     * @see InetAddress#getByAddress(byte[])
     */
    public abstract byte[] toBytes();

    /**
     * Useful for efficient conversion.
     *
     * @return the high part of the value
     */
    public abstract long highBits();

    /**
     * Useful for efficient conversion.
     *
     * @return the low part of the value
     */
    public abstract long lowBits();

    /**
     * Addition with overflow.
     *
     * @param addend the summand
     * @return sum
     */
    public abstract A add(A addend);

    /**
     * Subtraction with underflow.
     *
     * @param subtrahend number to subtract from this
     * @return difference
     */
    public abstract A subtract(A subtrahend);

    /**
     * Multiplication with overflow.
     *
     * @param multiplicand the factor
     * @return product
     */
    public abstract A multiply(A multiplicand);

    /**
     * Division.
     *
     * @param denominator the divisor
     * @return quotient
     * @throws ArithmeticException on divide-by-zero
     */
    public abstract A divide(A denominator);

    /**
     * Modulus.
     *
     * @param denominator the divisor
     * @return remainder
     * @throws ArithmeticException on divide-by-zero
     */
    public abstract A mod(A denominator);

    /**
     * Returns the IP address incremented by one, with overflow.
     *
     * @return the next IP address
     */
    public A next() {
        return add(family().fromUint(1));
    }

    /**
     * Returns the IP address decremented by one, with underflow.
     *
     * @return the previous IP address
     */
    public A prev() {
        return subtract(family().fromUint(1));
    }

    /**
     * Bitwise AND.
     *
     * @param mask the mask address
     * @return the AND'd address
     */
    public abstract A and(A mask);

    /**
     * Bitwise OR.
     *
     * @param mask the mask address
     * @return the OR'd address
     */
    public abstract A or(A mask);

    /**
     * Bitwise exclusive OR.
     *
     * @param mask the mask address
     * @return the XOR'd address
     */
    public abstract A xor(A mask);

    /**
     * Bitwise NOT.
     *
     * @return the inverse address
     */
    public abstract A not();

    /**
     * Bitwise shift.
     * Negative numbers more than {@link Family#bitWidth()} shift left.
     * Positive numbers less than {@link Family#bitWidth()} shift right.
     *
     * @param bits number of bits to shift
     * @return shifted value
     * @throws IllegalArgumentException if bits outside range
     */
    public abstract A shift(int bits);
}
