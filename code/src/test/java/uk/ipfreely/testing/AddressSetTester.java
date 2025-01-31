// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.testing;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.Block;
import uk.ipfreely.sets.Range;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public final class AddressSetTester {
    private AddressSetTester() {}

    public static <A extends Addr<A>> void test(AddressSet<A> set) {
        string(set);
        membership(set);
        ranges(set);
        blocks(set);
    }

    private static void string(AddressSet<?> set) {
        String actual = set.toString();
        assertTrue(actual.startsWith("{"));
        assertTrue(actual.endsWith("}"));
    }

    private static <A extends Addr<A>> void membership(AddressSet<A> set) {
        Iterator<A> it = set.iterator();
        for (int i = 0; i < 10; i++) {
            if (it.hasNext()) {
                A a = it.next();
                assertTrue(set.contains(a));
            }
        }

        boolean v6 = set.contains(Family.v6().min());
        boolean v4 = set.contains(Family.v4().min());
        if (v4 && v6) {
            fail("Can't contain both V4 and V6");
        }
    }

    private static <A extends Addr<A>> void ranges(AddressSet<A> set) {
        set.ranges().limit(10).forEach(AddressSetTester::range);
    }

    private static <A extends Addr<A>> void blocks(AddressSet<A> set) {
        set.ranges()
                .limit(10)
                .flatMap(Range::blocks)
                .limit(10)
                .forEach(AddressSetTester::block);
    }

    private static <A extends Addr<A>> void range(Range<A> r) {
        int i = r.first().compareTo(r.last());
        assertTrue(i <= 0);
    }

    private static <A extends Addr<A>> void block(Block<A> b) {
        A first = b.first();
        int i = first.compareTo(b.last());
        assertTrue(i <= 0);
        assertTrue(b.cidrNotation().contains("/"));
        assertTrue(b.maskSize() >= 0);
        assertTrue(b.maskSize() <= first.family().width());
        assertEquals(first, b.mask().and(first));
    }
}
