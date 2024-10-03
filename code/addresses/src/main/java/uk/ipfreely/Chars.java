// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

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
}
