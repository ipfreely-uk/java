// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
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
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, ten);
            AddressSet<V4> reverse = AddressSets.of(ten, zero);

            assertEquals(2, actual.ranges().count());
            assertEquals(reverse, actual);
        }
        {
            AddressSet<V4> actual = AddressSets.of(zero, AddressSets.address(v4().parse(0)));

            assertEquals(1, actual.ranges().count());
            assertEquals(BigInteger.ONE, actual.size());
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

//
//    @Test
//    void guarded() {
//        Block<V6> zero = AddressSets.address(v6().min());
//        Block<V6> fe80 = AddressSets.parseCidr(v6(), "fe80::/16");
//        Range<V6> range = AddressSets.range(v6().parse(10), v6().parse(999));
//        AddressSet<V6> array = AddressSets.of(zero, fe80, range);
//        {
//            AddressSet<V6> actual = AddressSets.guarded((AddressSet<V6>) fe80, v6().min());
//            assertInstanceOf(Block.class, actual, "block");
//            assertSame(actual, AddressSets.guarded(actual, v6().min()), "don't guard guarded");
//            assertNotSame(actual, AddressSets.guarded(actual, v6().min().next()), "different guard");
//            assertFalse(AddressSets.guarded(actual, v6().max()).toString().contains("Guard"), "remove guard");
//            GuardTester.test(actual.iterator(), 1);
//            GuardTester.test(actual.spliterator(), 1);
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded((AddressSet<V6>) range, v6().min());
//            assertInstanceOf(Range.class, actual, "range");
//            assertFalse(actual instanceof Block);
//            assertSame(actual, AddressSets.guarded(actual, v6().min()), "don't guard guarded");
//            assertNotSame(actual, AddressSets.guarded(actual, v6().min().next()), "different guard");
//            assertFalse(AddressSets.guarded(actual, v6().max()).toString().contains("Guard"), "remove guard");
//            GuardTester.test(actual.iterator(), 1);
//            GuardTester.test(actual.spliterator(), 1);
//            GuardTester.test(((Range<V6>) actual).stream(), 1);
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded(array, v6().min());
//            assertFalse(actual instanceof Range);
//            assertSame(actual, AddressSets.guarded(actual, v6().min()), "don't guard guarded");
//            assertNotSame(actual, AddressSets.guarded(actual, v6().min().next()), "different guard");
//            assertFalse(AddressSets.guarded(actual, v6().max()).toString().contains("Guard"), "remove guard");
//            GuardTester.test(actual.ranges(), 1);
//            GuardTester.test(actual.iterator(), 1);
//            GuardTester.test(actual.spliterator(), 1);
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded((AddressSet<V6>) zero, v6().min());
//            assertSame(zero, actual, "unguarded");
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded(zero, v6().max());
//            assertSame(zero, actual, "unguarded");
//        }
//        {
//            AddressSet<V6> internet = AddressSets.range(v6().min(), v6().max());
//            AddressSet<V6> actual = AddressSets.guarded(internet, v6().max());
//            assertSame(internet, actual, "unguarded");
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded(range, v6().max());
//            assertSame(range, actual, "unguarded");
//        }
//        {
//            AddressSet<V6> actual = AddressSets.guarded(array, v6().max());
//            assertSame(array, actual, "unguarded");
//        }
//    }

    @Test
    void string() {
        AddressSet<V4> set = large();
        String actual = set.toString();
        assertEquals('{', actual.charAt(0));
        assertTrue(actual.endsWith(";...}"));
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
//                AddressSets.guarded(fe80, hundred),
//                AddressSets.guarded(r, hundred),
//                AddressSets.guarded(array, hundred),
                AddressSets.of()
        };
        for (AddressSet<?> set : sets) {
            AddressSetTester.test(set);
        }
    }
}