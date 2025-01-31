// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.examples.tests;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.examples.RandomAddress;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Range;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomAddressTest {

    private static final Random RAN = new Random(0);

    @Test
    void generate() {
        {
            Addr<V4> address = RandomAddress.generate(Family.v4(), RAN::nextInt);
            assertNotNull(address);
        }
        {
            Addr<V6> address = RandomAddress.generate(Family.v6(), RAN::nextInt);
            assertNotNull(address);
        }
        {
            int actual = 3000;
            Addr<V4> address = RandomAddress.generate(Family.v4(), () -> actual);
            assertEquals(BigInteger.valueOf(actual), address.toBigInteger());
        }
        {
            int actual = 3000;
            Iterator<Integer> randoms = Arrays.asList(0, 0, 0, actual).iterator();
            Addr<V6> address = RandomAddress.generate(Family.v6(), randoms::next);
            assertEquals(BigInteger.valueOf(actual), address.toBigInteger());
        }
    }

    @Test
    void from() {
        {
            Range<V4> range = AddressSets.range(Family.v4().min(), Family.v4().max());
            Addr<V4> address = RandomAddress.from(range, RAN::nextInt);
            assertTrue(range.contains(address));
        }
    }
}
