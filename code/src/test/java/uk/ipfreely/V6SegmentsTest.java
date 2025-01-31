package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class V6SegmentsTest {

    @Test
    void forwards() {
        assertEquals(0x1234, V6Segments.forwards("1234", 0, 4).advance());
        assertEquals(0xFFFF, V6Segments.forwards("Ffff", 0, 4).advance());

        String input = "dead:beef:cafe:babe:baba:feed:1234";
        List<Long> expected = Arrays.asList(0xdeadL, 0xbeefL, 0xcafeL, 0xbabeL, 0xbabaL, 0xfeedL, 0x1234L);
        List<Long> actual = new ArrayList<>();
        V6Segments.Iter it = V6Segments.forwards(input, 0, input.length());
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(expected, actual);

        assertThrowsExactly(NoSuchElementException.class, () -> V6Segments.forwards("", 0, 0).next());
        assertThrowsExactly(ParseException.class, () -> V6Segments.forwards("foo", 0, 3).next());
        assertThrowsExactly(ParseException.class, () -> V6Segments.forwards("fffff", 0, 5).next());
        assertThrowsExactly(ParseException.class, this::walkInvalid);
    }

    private void walkInvalid() {
        String test = "123::456";
        V6Segments.Iter it = V6Segments.forwards(test, 0, test.length());
        while (it.hasNext()) {
            it.next();
        }
    }

    @Test
    void backwards() {
        assertEquals(0x1234, V6Segments.backwards("1234", 0, 4).advance());
        assertEquals(0xFFFF, V6Segments.backwards("Ffff", 0, 4).advance());

        String input = "dead:beef:cafe:babe:baba:feed:1234";
        List<Long> expected = Arrays.asList(0xdeadL, 0xbeefL, 0xcafeL, 0xbabeL, 0xbabaL, 0xfeedL, 0x1234L);
        Collections.reverse(expected);
        List<Long> actual = new ArrayList<>();
        V6Segments.Iter it = V6Segments.backwards(input, 0, input.length());
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(expected, actual);

        assertThrowsExactly(NoSuchElementException.class, () -> V6Segments.backwards("", 0, 0).next());
        assertThrowsExactly(ParseException.class, () -> V6Segments.backwards("foo", 0, 3).next());
        assertThrowsExactly(ParseException.class, () -> V6Segments.backwards("fffff", 0, 5).next());
        assertEquals(0, V6Segments.backwards("", 0, 0).advance());
    }
}