package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharsTest {

    @Test
    void appendNothing() {
        char[] one = new char[1];
        int offset = 0;
        int actual = Chars.append(one, offset, "");
        assertEquals(0, actual);
    }

    @Test
    void appendOne() {
        char[] one = new char[1];
        int offset = 0;
        int actual = Chars.append(one, offset, "A");
        assertEquals(1, actual);
        assertEquals('A', one[0]);
    }

    @Test
    void appendChar() {
        char[] one = new char[1];
        int offset = 0;
        int actual = Chars.append(one, offset, 'A');
        assertEquals(1, actual);
        assertEquals('A', one[0]);
    }


    @Test
    void splitNothing() {
        CharSequence[] actual = Chars.split("foo", ':');
        assertEquals(1, actual.length);
        assertEquals("foo", actual[0]);
    }

    @Test
    void splitN() {
        CharSequence[] actual = Chars.split("foo::bar:baz", ':');
        assertEquals(4, actual.length);
        assertEquals("foo", actual[0]);
        assertEquals("", actual[1]);
        assertEquals("bar", actual[2]);
        assertEquals("baz", actual[3]);
    }
}