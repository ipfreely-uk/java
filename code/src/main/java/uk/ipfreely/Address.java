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
 *          <td></td>
 *          <td>YES</td>
 *      </tr>
 *      <tr>
 *          <td>DNS host names</td>
 *          <td></td>
 *          <td>YES</td>
 *      </tr>
 *      <tr>
 *          <td>Numeric type</td>
 *          <td>YES</td>
 *          <td></td>
 *      </tr>
 *      <tr>
 *          <td>Generic type</td>
 *          <td>YES</td>
 *          <td></td>
 *      </tr>
 *      <tr>
 *          <td>Canonical string form (RFC5952)</td>
 *          <td>YES</td>
 *          <td></td>
 *      </tr>
 *      <tr>
 *          <td>{@link Comparable}</td>
 *          <td>YES</td>
 *          <td></td>
 *      </tr>
 *      <tr>
 *          <td>{@link java.io.Serializable}</td>
 *          <td></td>
 *          <td>YES</td>
 *      </tr>
 *  </table>
 *
 * <p>
 *     For {@link java.net.InetAddress} see the following libraries for overlapping utility methods:
 *     <a href="https://commons.apache.org/proper/commons-net/">Apache Commons Net</a>;
 *     <a href="https://guava.dev/">Google Guava</a>.
 * </p>
 *
 * <p>
 *     Inheritance outside the package is not supported.
 *     Future implementations may become
 *     <a href="https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html">sealed</a>.
 *     Future implementations may become
 *     <a href="https://openjdk.org/projects/valhalla/">value objects</a>.
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
