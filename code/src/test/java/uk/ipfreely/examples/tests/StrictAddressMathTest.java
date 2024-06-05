package uk.ipfreely.examples.tests;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.examples.StrictAddressMath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class StrictAddressMathTest {
    private V6 one = Family.v6().parse(1);
    private V6 two = Family.v6().parse(2);

    @Test
    void testAdd() {
        {
            V6 actual = StrictAddressMath.add(one, one);
            assertEquals(two, actual);
        }
        assertThrowsExactly(ArithmeticException.class, () -> {
            StrictAddressMath.add(one, Family.v6().max());
        });
    }

    @Test
    void testSubtract() {
        {
            V6 actual = StrictAddressMath.subtract(one, one);
            assertEquals(Family.v6().min(), actual);
        }
        assertThrowsExactly(ArithmeticException.class, () -> {
            StrictAddressMath.subtract(one, two);
        });
    }

    @Test
    void testMultiply() {
        {
            V6 actual = StrictAddressMath.multiply(two, one);
            assertEquals(two, actual);
        }
        assertThrowsExactly(ArithmeticException.class, () -> {
            StrictAddressMath.multiply(Family.v6().max(), two);
        });
    }
}
