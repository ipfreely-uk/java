// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
//package uk.ipfreely.sets;
//
//import org.junit.jupiter.api.Test;
//import uk.ipfreely.Family;
//import uk.ipfreely.V4;
//import uk.ipfreely.testing.SpliteratorTester;
//
//import java.util.Spliterator;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GuardedGeneralSpliteratorTest {
//
//    @Test
//    void spliterator() {
//        Range<V4> range = AddressSets.range(Family.v4().min(), Family.v4().max());
//        Spliterator<V4> s = new GuardedGeneralSpliterator<>(range.spliterator(), Family.v4().max(), a -> a);
//        SpliteratorTester.test(s);
//    }
//}
