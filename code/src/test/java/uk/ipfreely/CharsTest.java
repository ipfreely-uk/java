// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void indexOf() {
        assertEquals(4, Chars.indexOf("bar foo", "foo"));
        assertEquals(-1, Chars.indexOf("bar baz", "foo"));
        assertEquals(-1, Chars.indexOf("", "foo"));
        assertEquals(0, Chars.indexOf("foo bar", "foo"));
        assertEquals(0, Chars.indexOf("foo bar foo", "foo"));
    }

    @Test
    void view() {
        {
            CharSequence actual = Chars.view("", 0, 0);
            assertEquals("", actual.toString());
            assertEquals(0, actual.length());
            assertThrows(UnsupportedOperationException.class, () -> actual.subSequence(0, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> actual.charAt(0));
        }
        {
            CharSequence actual = Chars.view("foobar", 3, 3);
            assertEquals("bar", actual.toString());
            assertEquals(3, actual.length());
            assertEquals('b', actual.charAt(0));
            assertEquals('r', actual.charAt(2));
        }
    }

    @Test
    void concat() {
        {
            CharSequence actual = Chars.concat("foo", "bar");
            assertEquals("foobar", actual.toString());
            assertEquals(6, actual.length());
            assertEquals('f', actual.charAt(0));
            assertEquals('r', actual.charAt(5));
            assertThrows(UnsupportedOperationException.class, () -> actual.subSequence(0, 0));
        }
    }
}