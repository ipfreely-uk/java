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
 * Immutable IPv4 {@link Addr}ess and 32-bit unsigned integer value.
 * Use {@link Family#v4()} to create values.
 */
public final class V4 extends Addr<V4> {

    private static final V4[] LOWS = IntStream.rangeClosed(0, 256).mapToObj(V4::new).toArray(V4[]::new);
    private static final V4[] SPECIALS = initSpecialInterned();

    private static V4[] initSpecialInterned() {
        final List<V4> masks = V4MaskList.from(V4::new);
        final Set<V4> set = new HashSet<>(masks);
        set.add(new V4(Integer.MAX_VALUE));
        // add inverse masks
        for (V4 mask : masks) {
            set.add(new V4(~mask.value));
        }
        // add common loopback numbers
        set.add(new V4(fromInts(127, 0, 0, 0)));
        set.add(new V4(fromInts(127, 0, 0, 1)));
        // add common private
        set.add(new V4(fromInts(10, 0, 0, 0)));
        set.add(new V4(fromInts(172, 16, 0, 0)));
        set.add(new V4(fromInts(192, 168, 0, 0)));
        // remove low values
        asList(LOWS).forEach(set::remove);
        // to array
        return set.toArray(new V4[0]);
    }

    private static int fromInts(final int... arr) {
        // assert arr.length == Integer.SIZE / Byte.SIZE;

        return (arr[0] << 24)
                | (arr[1] << 16)
                | (arr[2] << 8)
                | arr[3];
    }

    private final int value;

    V4(int value) {
        this.value = value;
    }

    public Family<V4> family() {
        return V4Family.INST;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() == V4.class) {
            return ((V4) other).value == value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * IPv4 address in dotted quad notation.
     *
     * @return IP address as four base10 integers separated by periods
     */
    @Override
    public String toString() {
        return V4Strings.to(value);
    }

    /**
     * Useful for sorting.
     *
     * @param o another address
     * @return negative, zero, or positive integer as this is less than, equal to, or greater than other address
     */
    @Override
    public int compareTo(V4 o) {
        return Integer.compareUnsigned(value, o.value);
    }

    @Override
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(value & Consts.INT_MASK);
    }

    @Override
    public byte[] toBytes() {
        return V4Bytes.toBytes(value);
    }

    /**
     * The high bits are out of range for IPv4.
     *
     * @return zero
     */
    @Override
    public long highBits() {
        return 0;
    }

    /**
     * The IP address as n long between {@code 0L} and {@code 0xFFFFFFFFL}.
     *
     * @return the IP address as a long
     */
    @Override
    public long lowBits() {
        return Consts.INT_MASK & value;
    }

    @Override
    public int leadingZeros() {
        return Integer.numberOfLeadingZeros(value);
    }

    @Override
    public int trailingZeros() {
        return Integer.numberOfTrailingZeros(value);
    }

    @Override
    public double doubleValue() {
        final long MASK = 0xFFFFFFFFL;
        return value & MASK;
    }

    @Override
    public V4 add(V4 addend) {
        if (value == 0) {
            return addend;
        }
        if (addend.value == 0) {
            return this;
        }
        return fromInt(value + addend.value);
    }

    @Override
    public V4 subtract(V4 subtrahend) {
        return subtrahend.value == 0 ? this : fromInt(value - subtrahend.value);
    }

    @Override
    public V4 multiply(V4 multiplicand) {
        if (value == 1) {
            return multiplicand;
        }
        if (multiplicand.value == 1) {
            return this;
        }
        return fromInt(value * multiplicand.value);
    }

    @Override
    public V4 divide(V4 denominator) {
        if (value != 0 && denominator.value == 1) {
            return this;
        }
        return fromInt(Integer.divideUnsigned(value, denominator.value));
    }

    @Override
    public V4 mod(V4 denominator) {
        return fromInt(Integer.remainderUnsigned(value, denominator.value));
    }

    @Override
    public V4 and(V4 operand) {
        if (value == operand.value) {
            return this;
        }
        return fromInt(operand.value & value);
    }

    @Override
    public V4 or(V4 operand) {
        if (value == operand.value || operand.value == 0) {
            return this;
        }
        if (value == 0) {
            return operand;
        }
        return fromInt(operand.value | value);
    }

    @Override
    public V4 xor(V4 operand) {
        if (value == 0) {
            return operand;
        }
        if (operand.value == 0) {
            return this;
        }
        return fromInt(operand.value ^ value);
    }

    @Override
    public V4 not() {
        return fromInt(~value);
    }

    /**
     * @param bits -31 to 31
     * @return shifted number
     */
    @Override
    public V4 shift(int bits) {
        if (bits == 0 || value == 0) {
            return this;
        }
        int shifted = bits < 0
                ? value << -1 * bits
                : value >>> bits;
        return fromInt(shifted);
    }

    static int maskSizeIfBlock(final V4 first, final V4 last) {
        return V4Masking.maskSizeIfBlock(first.value, last.value);
    }

    static V4 fromInt(final int ip) {
        if (ip >= 0 && ip < LOWS.length) {
            return LOWS[ip];
        }
        for (V4 interned : SPECIALS) {
            if (ip == interned.value) {
                return interned;
            }
        }
        return new V4(ip);
    }
}
