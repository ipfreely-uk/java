package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class V6MaskingTest {

    @Test
    void maskSizeIfBlock() {
        long filled = 0xFFFFFFFFFFFFFFFFL;
        A zero = a(0, 0);
        A two = a(0, 2);
        A max = a(filled, filled);
        A half = a(0, filled);
        A ff = a(0, 0xFFL);
        A ffff = a(0, 0xFFFFL);
        A fe80_start = a(0xFE80000000000000L, 0L);
        A fe80_end = a(0xFE80000000000000L, filled);
        A fe90_start = a(0xFE90000000000000L, 0L);
        A fe90_end = a(0xFE90000000000000L, filled);
        A three_high = a(3, 0L);
        A four_high = a(4, filled);

        ensure(0, zero, max);
        ensure(128, zero, zero);
        ensure(128, max, max);
        ensure(120, zero, ff);
        ensure(112, zero, ffff);
        ensure(64, zero, half);
        ensure(64, fe80_start, fe80_end);
        ensure(64, fe90_start, fe90_end);

        ensure(-1, max, zero);
        ensure(-1, zero, two);
        ensure(-1, two, zero);
        ensure(-1, fe80_end, fe80_start);
        ensure(-1, fe80_start, fe90_end);
        ensure(-1, fe80_start, fe90_start);
        ensure(-1, three_high, four_high);
    }

    private void ensure(int expected, A first, A last) {
        int actual = V6Masking.maskSizeIfBlock(first.high, first.low, last.high, last.low);
        assertEquals(expected, actual);
    }

    private static A a(long high, long low) {
        return new A(high, low);
    }

    private static final class A {
        final long high;
        final long low;

        private A(long high, long low) {
            this.high = high;
            this.low = low;
        }
    }
}