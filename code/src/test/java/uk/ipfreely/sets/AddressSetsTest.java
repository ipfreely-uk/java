// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.testing.AddressSetTester;
import uk.ipfreely.testing.EqualsTester;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

class AddressSetsTest {

    @Test
    void of() {
        Range<V4> zero = AddressSets.address(v4().parse(0));
        Range<V4> ten = AddressSets.address(v4().parse(10));
        Range<V4> oneToNine = AddressSets.range(zero.first().next(), ten.first().prev());

        {
            AddressSet<V4> actual = AddressSets.of(zero, ten, oneToNine);

            assertEquals(1, actual.ranges().count());
            assertTrue(actual.contains(zero.first()));
            assertTrue(actual.contains(ten.first()));
            assertEquals(BigInteger.valueOf(11), actual.size());
            assertFalse(actual.isEmpty());
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, ten);
            AddressSet<V4> reverse = AddressSets.of(ten, zero);

            assertEquals(2, actual.ranges().count());
            assertEquals(reverse, actual);
            assertFalse(reverse.isEmpty());
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, AddressSets.address(v4().parse(0)));

            assertEquals(1, actual.ranges().count());
            assertEquals(BigInteger.ONE, actual.size());
            assertFalse(actual.isEmpty());
        }
    }

    @Test
    void equality() {
        Range<V4> zero = AddressSets.address(v4().parse(0));
        Range<V4> ten = AddressSets.address(v4().parse(10));
        Range<V4> oneToNine = AddressSets.range(zero.first().next(), ten.first().prev());
        Range<V4> hundred = AddressSets.address(v4().parse(100));
        Range<V4> eleven = AddressSets.address(v4().parse(11));

        EqualsTester.test(
                new Object(),
                AddressSets.of(zero),
                AddressSets.of(ten),
                AddressSets.of(),
                AddressSets.of(oneToNine),
                AddressSets.of(oneToNine),
                AddressSets.of(ten, zero),
                AddressSets.of(zero, ten),
                AddressSets.of(zero),
                AddressSets.of(hundred, zero, ten, ten),
                AddressSets.of(hundred, zero, eleven),
                AddressSets.of(ten, oneToNine, zero),
                large(),
                large()
        );
    }

    @Test
    void empty() {
        AddressSet<V4> empty = AddressSets.of();
        assertEquals(BigInteger.ZERO, empty.size());
        assertFalse(empty.iterator().hasNext());
        assertEquals(0L, empty.ranges().count());
        assertTrue(empty.isEmpty());
    }

    @Test
    void contains() {
        List<V4> minMax = Arrays.asList(v4().min(), v4().max());

        AddressSet<V4> actual = new AddressSet<V4>() {
            @Override
            public Stream<Range<V4>> ranges() {
                return minMax.stream().map(AddressSets::address);
            }

            @Override
            public Iterator<V4> iterator() {
                return minMax.iterator();
            }
        };

        assertTrue(actual.contains(v4().min()));
        assertTrue(actual.contains(v4().max()));
    }

    @Test
    void string() {
        AddressSet<V4> set = large();
        String actual = set.toString();
        assertEquals('{', actual.charAt(0));
        String expected = ", [1000...]}";
        String end = actual.substring(actual.length() - expected.length());
        assertEquals(expected, end);
    }

    private AddressSet<V4> large() {
        List<Range<V4>> list = new ArrayList<>();
        Range<V4> r = AddressSets.address(v4().min());
        for (int i = 0; i < 1000; i++) {
            list.add(r);
            r = AddressSets.address(r.first().add(v4().parse(10)));
        }
        return AddressSets.from(list);
    }

    @Test
    void sets() {
        V6 hundred = v6().parse(100);
        Block<V6> one = AddressSets.address(v6().parse(1));
        Block<V6> fe80 = AddressSets.block(v6().parse("fe80::"), 16);
        Range<V6> r = AddressSets.range(v6().parse("a::10"), v6().parse("a::110"));
        AddressSet<V6> array = AddressSets.of(one, fe80, r);
        AddressSet<?>[] sets = {
                AddressSets.block(v6().min(), v6().max()),
                AddressSets.block(v4().min(), v4().max()),
                one,
                fe80,
                r,
                array,
                AddressSets.of()
        };
        for (AddressSet<?> set : sets) {
            AddressSetTester.test(set);
        }
    }

    @Test
    void collector() {
        {
            AddressSet<V4> expected = AddressSets.range(Family.v4().min(), Family.v4().parse(1024 * 1024));
            AddressSet<V4> actual = expected.addresses()
                    .map(AddressSets::address)
                    .collect(AddressSets.collector());
            assertEquals(expected, actual);
        }
        {
            AddressSet<V4> expected = AddressSets.range(Family.v4().min(), Family.v4().parse(1024));
            AddressSet<V4> as1 = expected.addresses()
                    .filter(this::even)
                    .map(AddressSets::address)
                    .collect(AddressSets.collector());
            AddressSet<V4> as2 = expected.addresses()
                    .filter(this::odd)
                    .map(AddressSets::address)
                    .collect(AddressSets.collector());
            AddressSet<V4> actual = AddressSets.of(as1, as2);
            assertEquals(expected, actual);
        }
    }

    private boolean even(Addr<?> a) {
        return a.lowBits() % 2 == 0;
    }

    private boolean odd(Addr<?> a) {
        return a.lowBits() % 2 == 1;
    }
}
