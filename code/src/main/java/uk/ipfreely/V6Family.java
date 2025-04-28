// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

final class V6Family extends Family<V6> {

    private static final BigInteger SIZE = BigInteger.valueOf(2).pow(Consts.V6_WIDTH);
    private static final BigInteger MAX_VALUE = SIZE.subtract(BigInteger.ONE);
    private static final String MAX_ASSERTION = "Maximum value is " + MAX_VALUE;

    static final Family<V6> INST = new V6Family();

    private V6Family() {}

    @Override
    public String toString() {
        return "IPv6";
    }

    @Override
    public V6 parse(long high, long low) {
        return V6.fromLongs(high, low);
    }

    @Override
    public V6 parse(int unsigned) {
        return V6.fromLongs(0, unsigned & Consts.INT_MASK);
    }

    @Override
    public V6 parse(CharSequence candidate) {
        for (int i = candidate.length() - 1; i >= 0; i--) {
            char ch = candidate.charAt(i);
            if (ch == ':') {
                break;
            }
            if (ch == '.') {
                return V6Strings.parse4In6(candidate, V6::fromLongs);
            }
        }
        return V6Strings.parse(candidate, V6::fromLongs);
    }

    @Override
    public V6 parse(byte... ip) {
        validate(ip.length == 16, "Ip6 128 bit addresses are 16 bytes", ip, ParseException::new);
        return V6Bytes.fromBytes(V6::fromLongs, ip);
    }

    @Override
    public V6 parse(BigInteger ip) {
        validate(BigInteger.ZERO.compareTo(ip) <= 0, "Minimum value is 0", ip, ParseException::new);
        validate(MAX_VALUE.compareTo(ip) >= 0, MAX_ASSERTION, ip, ParseException::new);
        return V6BigIntegers.fromBigInteger(V6::fromLongs, ip);
    }

    @Override
    public List<V6> masks() {
        return V6Masks.MASKS;
    }

    @Override
    public int maskBitsForBlock(V6 first, V6 last) {
        return V6Masking.maskSizeIfBlock(first.highBits(), first.lowBits(), last.highBits(), last.lowBits());
    }

    @Override
    public BigInteger maskAddressCount(int maskBits) {
        validate(maskBits >= 0 && maskBits <= width(), "Invalid mask size", maskBits, IllegalArgumentException::new);

        return InternedMaskSizes.v6(maskBits);
    }

    @Override
    public int width() {
        return Consts.V6_WIDTH;
    }

    @Override
    public Class<V6> type() {
        return V6.class;
    }
}
