// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

final class V4Family extends Family<V4> {

    private static final BigInteger MAX_VALUE = BigInteger.valueOf(2).pow(Consts.V4_WIDTH).subtract(BigInteger.ONE);
    private static final String MAX_ASSERTION = "Maximum value is " + MAX_VALUE;

    static final Family<V4> INST = new V4Family();

    private V4Family() {}

    @Override
    public String toString() {
        return "IPv4";
    }

    @Override
    public V4 parse(long highBits, long lowBits) {
        validate(highBits == 0, "Out of range: high bits must be zero", highBits, ParseException::new);
        validate(lowBits >= 0 && lowBits <= 0xFFFFFFFFL, "Out of range: max value is " + 0xFFFFFFFFL, lowBits, ParseException::new);
        return V4.fromInt((int) lowBits);
    }

    @Override
    public V4 parse(CharSequence candidate) {
        int n = V4Strings.from(candidate);
        return V4.fromInt(n);
    }

    @Override
    public V4 parse(byte... ip) {
        validate(ip.length == 4, "Invalid address; Ip4 32 bit addresses are 4 bytes", ip, ParseException::new);
        final int uint32 = V4Bytes.fromBytes(ip);
        return V4.fromInt(uint32);
    }

    @Override
    public V4 parse(BigInteger ip) {
        validate(BigInteger.ZERO.compareTo(ip) <= 0, "Minimum value is 0", ip, ParseException::new);
        validate(MAX_VALUE.compareTo(ip) >= 0, MAX_ASSERTION, ip, ParseException::new);
        return V4.fromInt(ip.intValue());
    }

    @Override
    public V4 parse(int unsigned) {
        return V4.fromInt(unsigned);
    }

    @Override
    public int width() {
        return Consts.V4_WIDTH;
    }

    @Override
    public Class<V4> type() {
        return V4.class;
    }

    @Override
    public List<V4> masks() {
        return V4Masks.MASKS;
    }

    @Override
    public int maskBitsForBlock(V4 first, V4 last) {
        return V4.maskSizeIfBlock(first, last);
    }

    @Override
    public BigInteger maskAddressCount(int maskBits) {
        validate(maskBits >= 0 && maskBits <= width(), "Invalid mask size", maskBits, IllegalArgumentException::new);

        return InternedMaskSizes.v4(maskBits);
    }
}
