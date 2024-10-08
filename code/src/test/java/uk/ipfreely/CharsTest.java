// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
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
    void indexOf() {
        assertEquals(4, Chars.indexOf("bar foo", "foo"));
        assertEquals(-1, Chars.indexOf("bar baz", "foo"));
        assertEquals(-1, Chars.indexOf("", "foo"));
        assertEquals(0, Chars.indexOf("foo bar", "foo"));
        assertEquals(0, Chars.indexOf("foo bar foo", "foo"));
    }
}