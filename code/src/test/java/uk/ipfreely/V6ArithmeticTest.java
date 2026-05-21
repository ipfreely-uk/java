package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V6ArithmeticTest {

    @Test
    void divide() {
        {
            var n = V6BigIntegers.toBigInteger(0xF, 0xF);
            var d = V6BigIntegers.toBigInteger(0, 0xFFFF_FFFF_FFFF_FFFFL);
            var q = n.mod(d);
            var expected = V6BigIntegers.fromBigInteger(Result::new, q);
            var actual = V6Arithmetic.divide(Result::new, 0xF, 0xF, 0, 0xFFFF_FFFF_FFFF_FFFFL, true);
            assertEquals(expected, actual);
        }
        {
            var expected = new Result(0, 3);
            var actual = V6Arithmetic.divide(Result::new, 0, 12, 0, 4, false);
            assertEquals(expected, actual);
        }
        {
            var expected = new Result(0, 1);
            var actual = V6Arithmetic.divide(Result::new, 0, 13, 0, 4, true);
            assertEquals(expected, actual);
        }
    }

    private record Result(long h, long l) {}
}