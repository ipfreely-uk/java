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
