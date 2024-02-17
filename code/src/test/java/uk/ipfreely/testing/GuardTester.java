package uk.ipfreely.testing;

import uk.ipfreely.collections.ExcessiveIterationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class GuardTester {
    private GuardTester() {}

    public static <E> List<E> test(Stream<E> stream, int expected) {
        return test(stream.spliterator(), expected);
    }

    public static <E> List<E> test(Iterator<E> it, int expected) {
        return consume(it::forEachRemaining, expected);
    }

    public static <E> List<E> test(Spliterator<E> s, int expected) {
        return consume(s::forEachRemaining, expected);
    }

    private static <E> List<E> consume(Consumer<Consumer<E>> c, int expected) {
        List<E> list = new ArrayList<>();
        try {
            c.accept(new TooMuch<>(list::add, expected));
            fail();
        } catch (ExcessiveIterationException e) {
            // expected
        }
        assertEquals(expected, list.size());
        return list;
    }

    private static class TooMuch<E> implements Consumer<E> {
        private int count;
        private final Consumer<E> c;
        private final int expected;

        private TooMuch(Consumer<E> c, int expected) {
            this.c = c;
            this.expected = expected;
        }

        @Override
        public void accept(E e) {
            if (count > expected) {
                fail();
            }
            count++;
            c.accept(e);
        }
    }
}
