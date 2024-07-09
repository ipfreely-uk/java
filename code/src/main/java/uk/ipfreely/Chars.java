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

    static CharSequence[] split(CharSequence cs, char match) {
        int count = 0;
        for (int i = 0, len = cs.length(); i < len; i++) {
            if (cs.charAt(i) == match) {
                count++;
            }
        }

        CharSequence[] result = new String[count + 1];
        int offset = 0;
        int index = 0;
        for (int i = 0, len = cs.length(); i < len; i++) {
            if (cs.charAt(i) == match) {
                result[index++] = cs.subSequence(offset, i);
                offset = i + 1;
            }
        }
        result[index] = cs.subSequence(offset, cs.length());

        return result;
    }

    static int indexOf(CharSequence cs, CharSequence find) {
        final int len = find.length();
        outer: for (int i = 0, limit = cs.length() - len + 1; i < limit; i++) {
            for (int j = 0; j < len; j++) {
                char l = cs.charAt(i + j);
                char r = find.charAt(j);
                if (l != r) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

}
