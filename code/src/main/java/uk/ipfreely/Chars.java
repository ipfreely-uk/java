// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import static uk.ipfreely.Validation.validate;

final class Chars {

    private Chars() {}

    static int append(char[] buf, int offset, String value) {
        // assert value.length() <= buf.length - offset;
        for (int i = 0, len = value.length(); i < len; i++) {
            buf[offset++] = value.charAt(i);
        }
        return offset;
    }

    static int append(char[] buf, int offset, char value) {
        // assert buf.length - offset >= 1;
        buf[offset++] = value;
        return offset;
    }

    static int indexOf(CharSequence cs, CharSequence find) {
        final int len = find.length();
        for (int i = 0, limit = cs.length() - len + 1; i < limit; i++) {
            if (match(cs, i, find, len)) {
                return i;
            }
        }
        return -1;
    }

    static int lastIndexOf(CharSequence cs, char find) {
        for (int i = cs.length() - 1; i >= 0; i--) {
            if (cs.charAt(i) == find) {
                return i;
            }
        }
        return -1;
    }

    private static boolean match(CharSequence cs, int offset, CharSequence find, int len) {
        for (int i = 0; i < len; i++) {
            char l = cs.charAt(i + offset);
            char r = find.charAt(i);
            if (l != r) {
                return false;
            }
        }
        return true;
    }

    static CharSequence view(CharSequence cs, int offset, int length) {
        validate(offset >= 0, "Out of bounds", cs, IllegalArgumentException::new);
        validate(length + offset <= cs.length(), "Out of bounds", cs, IllegalArgumentException::new);

        class View implements CharSequence {

            @Override
            public int length() {
                return length;
            }

            @Override
            public char charAt(int index) {
                if (index >= length) {
                    throw new IndexOutOfBoundsException();
                }
                return cs.charAt(offset + index);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return cs.subSequence(offset, offset + length).toString();
            }
        }

        return new View();
    }

    static CharSequence concat(CharSequence c0, CharSequence c1) {

        class Concatenated implements CharSequence {

            @Override
            public int length() {
                return c0.length() + c1.length();
            }

            @Override
            public char charAt(int index) {
                int c0l = c0.length();
                if (index < c0l) {
                    return c0.charAt(index);
                }
                return c1.charAt(index - c0l);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return c0.toString() + c1;
            }
        }

        return new Concatenated();
    }
}
