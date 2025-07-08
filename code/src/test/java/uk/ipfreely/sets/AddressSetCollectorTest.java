// Copyright 2025 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressSetCollectorTest {

    @Test
    void combiner() {
        AddressSet<V4> home1 = AddressSets.parseCidr(Family.v4(), "127.0.0.1/32");
        AddressSet<V4> home2 = AddressSets.parseCidr(Family.v4(), "127.0.0.2/32");

        AddressSetCollector<V4> collector = AddressSetCollector.impl();
        Collection<AddressSet<V4>> one = collector.supplier().get();
        one.add(home1);
        Collection<AddressSet<V4>> two = collector.supplier().get();
        two.add(home2);

        Collection<AddressSet<V4>> actual = collector.combiner().apply(one, two);
        assertTrue(actual.containsAll(Arrays.asList(home1, home2)));
    }
}