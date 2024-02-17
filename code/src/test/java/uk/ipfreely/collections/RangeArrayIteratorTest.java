package uk.ipfreely.collections;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;

class RangeArrayIteratorTest {

    @Test
    void next() {
        V4 one = v4().fromUint(1);
        V4 three = v4().fromUint(3);
        {
            Range<V4> range = Ranges.from(one, three);
            List<V4> actual = new ArrayList<>();

            Iterator<V4> it = new RangeArrayIterator<>(range);
            it.forEachRemaining(actual::add);

            assertEquals(3, actual.size());
        }
        {
            Range<V4> r1 = Ranges.address(one);
            Range<V4> r3 = Ranges.address(three);
            List<V4> actual = new ArrayList<>();

            Iterator<V4> it = new RangeArrayIterator<>(r1, r3);
            it.forEachRemaining(actual::add);

            assertEquals(2, actual.size());
        }
    }
}