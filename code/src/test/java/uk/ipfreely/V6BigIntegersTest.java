// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class V6BigIntegersTest {

    @Test
    void toBigInteger() {
        {
            long max = 0xFFFFFFFFFFFFFFFFL;
            var expected = BigInteger.TWO.pow(128).subtract(BigInteger.ONE);
            var actual = V6BigIntegers.toBigInteger(max, max);
            assertEquals(expected, actual);
        }
        {
            var expected = BigInteger.TEN;
            var actual = V6BigIntegers.toBigInteger(0, 10);
            assertEquals(expected, actual);
        }
    }

    @Test
    void fromBigInteger() {
        record Actual(long high, long low){}
        {
            long max = 0xFFFFFFFFFFFFFFFFL;
            var expected = BigInteger.TWO.pow(128).subtract(BigInteger.ONE);
            var actual = V6BigIntegers.fromBigInteger(Actual::new, expected);
            assertEquals(max, actual.high);
            assertEquals(max, actual.low);
        }
        {
            var actual = V6BigIntegers.fromBigInteger(Actual::new, BigInteger.TEN);
            assertEquals(0, actual.high);
            assertEquals(10, actual.low);
        }
    }
}