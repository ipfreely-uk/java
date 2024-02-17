package uk.ipfreely;

import static uk.ipfreely.Chars.append;
import static uk.ipfreely.Validation.validate;

final class V4Strings {

    private V4Strings() {}

    /**
     * @param value IP address as int
     * @return canonical string form
     */
    static String to(int value) {
        // 200.200.200.200
        final int MAX = 3 * 4 + 3;
        char[] buf = new char[MAX];
        int offset = 0;

        offset = qaud(buf, offset, value, 3 * Byte.SIZE);
        offset = append(buf, offset, '.');
        offset = qaud(buf, offset, value, 2 * Byte.SIZE);
        offset = append(buf, offset, '.');
        offset = qaud(buf, offset, value, Byte.SIZE);
        offset = append(buf, offset, '.');
        offset = qaud(buf, offset, value, 0);

        return new String(buf, 0, offset);
    }

    private static int qaud(char[] buf, int off, int value, int shift) {
        int quad = (value >>> shift) & 0xFF;
        int len = off;
        if (quad >= 100) {
            len = Chars.append(buf, len, (char) (quad / 100 + '0'));
            quad %= 100;
            len = Chars.append(buf, len, (char) (quad / 10 + '0'));
            quad %= 10;
            len = Chars.append(buf, len, (char) (quad + '0'));
        } else if (quad >= 10) {
            len = Chars.append(buf, len, (char) (quad / 10 + '0'));
            quad %= 10;
            len = Chars.append(buf, len, (char) (quad + '0'));
        } else {
            len = Chars.append(buf, len, (char) (quad + '0'));
        }
        return len;
    }

    /**
     * @param address IPv4 address to parse
     * @return address as int
     */
    static int from(CharSequence address) {
        final int len = address.length();

        validate(address.charAt(0) != '.', "Leading dot", address, ParseException::new);
        validate(address.charAt(len - 1) != '.', "Trailing dot", address, ParseException::new);

        // these two for validation
        char last = '-';
        int dots = 0;
        // values
        int result = 0;
        int quad = 0;

        for (int i = 0; i < len; i++) {
            char ch = address.charAt(i);
            validate(ipChar(ch), "Invalid character", address, ParseException::new);

            if (ch == '.') {
                dots++;
                validate(last != '.', "Missing digits", address, ParseException::new);

                result <<= Byte.SIZE;
                result |= quad;
                quad = 0;
            } else {
                quad = quad * 10 + ch - '0';
            }

            last = ch;
        }

        // append last quad
        result <<= Byte.SIZE;
        result |= quad;

        validate(dots == 3, "3 dots required", address, ParseException::new);

        return result;
    }

    private static boolean ipChar(char n) {
        return n == '.' || (n >= '0' && n <= '9');
    }
}
