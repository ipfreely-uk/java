package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.testing.SpliteratorTester;

import java.util.Spliterator;

class SubnetSpliteratorTest {

    @Test
    void spliterator() {
        V4 zero = Family.v4().min();
        V4 one = zero.next();
        V4 ten = Family.v4().fromUint(10);
        Spliterator<Block<V4>> s = new SubnetSpliterator<>(zero, ten, one);
        SpliteratorTester.test(s);
    }
}
