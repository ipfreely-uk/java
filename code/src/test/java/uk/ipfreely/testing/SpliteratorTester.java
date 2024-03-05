// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.testing;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class SpliteratorTester {
    private SpliteratorTester() {}

    public static <T> void test(Spliterator<T> s) {
        s.characteristics();
        s.estimateSize();
        s.tryAdvance(new AtomicReference<>()::set);
        s.trySplit();
    }
}
