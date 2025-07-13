// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

/**
 * Immutable IPv6 {@link Addr}ess and 128-bit unsigned integer value.
 * Use {@link Family#v6()} to create values.
 */
public final class V6 extends Addr<V6> {

    private static final V6[] LOWS = IntStream.rangeClosed(0, 256).mapToObj(i -> new V6(0, i)).toArray(V6[]::new);
    private static final V6[] SPECIALS = initInterned();

    private static V6[] initInterned() {
        final List<V6> masks = V6MaskList.from(V6::new);
        final Set<V6> set = new HashSet<>(masks);
        for (V6 mask : masks) {
            set.add(new V6(~mask.high, ~mask.low));
        }
        // link local
        set.add(new V6(0xfe80000000000000L, 0));
        // documentation
        set.add(new V6(0x20010db800000000L, 0));
        asList(LOWS).forEach(set::remove);
        return set.toArray(new V6[0]);
    }

    private final long high;
    private final long low;

    V6(long high, long low) {
        this.high = high;
        this.low = low;
    }

    @Override
    public Family<V6> family() {
        return V6Family.INST;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() == V6.class) {
            V6 v = (V6) other;
            return v.high == high && v.low == low;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (high * 31 + low);
    }

    /**
     * See <a target="_top" href="https://tools.ietf.org/html/rfc5952">A Recommendation for IPv6 Address Text Representation</a>.
     *
     * @return the address in RFC5952 format
     */
    @Override
    public String toString() {
        return V6Strings.toIpv6String(high, low);
    }

    /**
     * Useful for sorting.
     *
     * @param o another address
     * @return negative, zero, or positive integer as this is less than, equal to, or greater than other address
     */
    @Override
    public int compareTo(V6 o) {
        final int cu = Long.compareUnsigned(high, o.high);
        return (cu == 0) ? Long.compareUnsigned(low, o.low) : cu;
    }

    @Override
    public BigInteger toBigInteger() {
        return V6BigIntegers.toBigInteger(high, low);
    }

    @Override
    public byte[] toBytes() {
        return V6Bytes.toBytes(high, low);
    }

    @Override
    public long highBits() {
        return high;
    }

    @Override
    public long lowBits() {
        return low;
    }

    @Override
    public int leadingZeros() {
        if (high == 0) {
            return Long.numberOfLeadingZeros(low) + Long.SIZE;
        }
        return Long.numberOfLeadingZeros(high);
    }

    @Override
    public int trailingZeros() {
        if (low == 0) {
            return Long.numberOfTrailingZeros(high) + Long.SIZE;
        }
        return Long.numberOfTrailingZeros(low);
    }

    @Override
    public double doubleValue() {
        return toBigInteger().doubleValue();
    }

    @Override
    public V6 add(V6 addend) {
        if (isZero(this)) {
            return addend;
        }
        if (isZero(addend)) {
            return this;
        }
        return V6Arithmetic.add(V6::fromLongs, high, low, addend.high, addend.low);
    }

    @Override
    public V6 subtract(V6 subtrahend) {
        if (isZero(subtrahend)) {
            return this;
        }
        return V6Arithmetic.subtract(V6::fromLongs, high, low, subtrahend.high, subtrahend.low);
    }

    @Override
    public V6 multiply(V6 multiplicand) {
        if (isOne(this)) {
            return multiplicand;
        }
        if (isOne(multiplicand)) {
            return this;
        }
        if (isZero(this) || isZero(multiplicand)) {
            return fromLongs(0, 0);
        }
        if (isTwo(this)) {
            return multiplicand.add(multiplicand);
        }
        if (isTwo(multiplicand)) {
            return this.add(this);
        }
        // TODO: use Math.unsignedMultiplyHigh when Java 8 deprecated
        return V6Arithmetic.multiply(V6::fromLongs, high, low, multiplicand.high, multiplicand.low);
    }

    @Override
    public V6 divide(V6 denominator) {
        if (isZero(denominator)) {
            throw new ArithmeticException("divide by zero");
        }
        if (isOne(denominator)) {
            return this;
        }
        if (high == 0 && denominator.high == 0) {
            long newLow = Long.divideUnsigned(low, denominator.low);
            return fromLongs(0, newLow);
        }
        final int compare = compareTo(denominator);
        if (compare == 0) {
            return fromLongs(0, 1);
        }
        if (compare < 0) {
            return fromLongs(0, 0);
        }
        BigInteger val = toBigInteger().divide(denominator.toBigInteger());
        return V6BigIntegers.fromBigInteger(V6::fromLongs, val);
    }

    @Override
    public V6 mod(V6 denominator) {
        if(isZero(denominator)) {
            throw new ArithmeticException("divide by zero");
        }
        final int compare = compareTo(denominator);
        if (compare == 0 || isOne(denominator)) {
            return fromLongs(0, 0);
        }
        if (compare < 0) {
            return this;
        }
        if (high == 0 && denominator.high == 0) {
            long remainder = Long.remainderUnsigned(low, denominator.low);
            return fromLongs(0, remainder);
        }
        V6 quotient = divide(denominator);
        V6 nearest = quotient.multiply(denominator);
        return subtract(nearest);
    }

    @Override
    public V6 and(V6 operand) {
        if (high == operand.high && low == operand.low) {
            return this;
        }
        return fromLongs(operand.high & high, operand.low & low);
    }

    @Override
    public V6 or(V6 operand) {
        if (isZero(this)) {
            return operand;
        }
        if (equals(operand) || isZero(operand)) {
            return this;
        }
        return fromLongs(operand.high | high, operand.low | low);
    }

    @Override
    public V6 xor(V6 operand) {
        if (isZero(this)) {
            return operand;
        }
        if (isZero(operand)) {
            return this;
        }
        return fromLongs(operand.high ^ high, operand.low ^ low);
    }

    @Override
    public V6 not() {
        return fromLongs(~high, ~low);
    }

    /**
     * @param bits -127 to 127
     * @return shifted value
     */
    @Override
    public V6 shift(int bits) {
        if (bits == 0 || isZero(this)) {
            return this;
        }
        return bits < 0
                ? shiftLeft(bits * -1)
                : shiftRight(bits);
    }

    private V6 shiftRight(final int bits) {
        int n = bits % Consts.V6_WIDTH;
        if (n == Long.SIZE) {
            return fromLongs(0, high);
        }
        if (n > Long.SIZE) {
            return fromLongs(0, high >>> (n - Long.SIZE));
        }
        long x = high << (Long.SIZE - n);
        return fromLongs(high >>> n, (low >>> n) | x);
    }

    private V6 shiftLeft(final int bits) {
        int n = bits % Consts.V6_WIDTH;
        if (n == Long.SIZE) {
            return fromLongs(low, 0);
        }
        if (n > Long.SIZE) {
            return fromLongs(low << (n - Long.SIZE), 0);
        }
        long x = low >>> (Long.SIZE - n);
        return fromLongs((high << n) | x, low << n);
    }

    private static boolean isZero(V6 ip) {
        return ip.high == 0 && ip.low == 0;
    }

    private static boolean isOne(V6 ip) {
        return ip.high == 0 && ip.low == 1;
    }

    private static boolean isTwo(V6 ip) {
        return ip.high == 0 && ip.low == 2;
    }

    static V6 fromLongs(final long highBits, final long lowBits) {
        if (highBits == 0 && lowBits >= 0 && lowBits < LOWS.length) {
            return LOWS[(int) lowBits];
        }
        for (V6 interned : SPECIALS) {
            if (highBits == interned.high && lowBits == interned.low) {
                return interned;
            }
        }
        return new V6(highBits, lowBits);
    }
}
