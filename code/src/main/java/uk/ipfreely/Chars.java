package uk.ipfreely;

final class Chars {

    private Chars() {}

    static int append(char[] buf, int offset, String value) {
        assert value.length() <= buf.length - offset;
        for (int i = 0, len = value.length(); i < len; i++) {
            buf[offset++] = value.charAt(i);
        }
        return offset;
    }

    static int append(char[] buf, int offset, char value) {
        assert buf.length - offset >= 1;
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
}
