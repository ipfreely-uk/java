// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 * <p>
 *     Abstract IP address type implemented by {@link V4} and {@link V6}.
 *     Use {@link Family} to create instances.
 * </p>
 * <p>
 *     Future implementations may become
 *     <a target="_top" href="https://openjdk.org/projects/valhalla/">value objects</a>.
 * </p>
 *
 * <h2>Usage Hints</h2>
 *
 * <table border="1">
 *      <caption>Comparison with {@link InetAddress}</caption>
 *      <tr>
 *          <th>Feature</th>
 *          <th>{@link Addr}</th>
 *          <th>{@link InetAddress}</th>
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
 *     For {@link InetAddress} see the following libraries for overlapping utility methods:
 *     <a target="_top" href="https://commons.apache.org/proper/commons-net/">Apache Commons Net</a>;
 *     <a target="_top" href="https://guava.dev/">Google Guava</a>.
 * </p>
 *
 * @param <A> the address type
 */
public abstract sealed class Addr<A extends Addr<A>> implements Comparable<A> permits V4, V6 {
    Addr() {}

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
     * The address as bytes of length {@link Family#width()} / {@link Byte#SIZE}.
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
     * Similar to {@link Integer#numberOfLeadingZeros(int)}.
     *
     * @return number of zero bits preceding the highest-order ("leftmost") one-bit
     */
    public abstract int leadingZeros();

    /**
     * Similar to {@link Integer#numberOfTrailingZeros(int)}.
     *
     * @return number of zero bits following the lowest-order ("rightmost") one-bit
     */
    public abstract int trailingZeros();

    /**
     * Like {@link Number#doubleValue()}.
     *
     * @return approximate floating point value
     */
    public abstract double doubleValue();

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
        return add(family().parse(1));
    }

    /**
     * Returns the IP address decremented by one, with underflow.
     *
     * @return the previous IP address
     */
    public A prev() {
        return subtract(family().parse(1));
    }

    /**
     * Bitwise AND.
     * <pre><code>
     *     // EXAMPLE
     *     // LinkLocal is fe80::/10
     *     private static V6 LL = Family.v6().parse("fe80::");
     *     private static V6 MASK = Family.v6().subnets().masks().get(10);
     *
     *     public static boolean isLinkLocal(V6 candidate) {
     *         return MASK.and(candidate).equals(LL);
     *     }
     * </code></pre>
     *
     * @param operand the mask address
     * @return the AND'd address
     */
    public abstract A and(A operand);

    /**
     * Bitwise OR.
     *
     * @param operand the mask address
     * @return the OR'd address
     */
    public abstract A or(A operand);

    /**
     * Bitwise exclusive OR.
     *
     * @param operand the mask address
     * @return the XOR'd address
     */
    public abstract A xor(A operand);

    /**
     * Bitwise NOT.
     * <pre><code>
     *     // EXAMPLE
     *     // Define 192.168.0.0/24
     *     int maskBits = 24;
     *     V4 networkAddress = Family.v4().parse("192.168.0.0");
     *     // 255.255.255.0
     *     V4 mask = Family.v4().subnets().masks().get(maskBits);
     *     // 0.0.0.255
     *     V4 inverseMask = mask.not()
     *     // 192.168.0.255
     *     V4 lastAddress = inverseMask.or(networkAddress);
     * </code></pre>
     *
     * @return the complement
     */
    public abstract A not();

    /**
     * Bitwise shift.
     * Negative operands shift left.
     * Positive operands shift right.
     * Values exceeding {@link Family#width()} overflow as described
     * in The Java Language Specification Java SE 21 Edition
     * <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.19" target="_top">15.19. Shift Operators</a>.
     * <pre><code>
     *     // EXAMPLE
     *     // 0.0.0.1
     *     V4 one = Family.v4().parse(1);
     *     // 0.0.0.2
     *     V4 two = one.shift(-1);
     * </code></pre>
     *
     * @param bits number of bits to shift
     * @return shifted value
     */
    public abstract A shift(int bits);
}
