// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import static uk.ipfreely.Validation.validate;

final class V6Strings {

    private static final int IP6_SEGMENTS = 8;

    private V6Strings() {}

    static String toIpv6String(final long high, final long low) {
        int z0 = -1;
        int zn = -1;
        for (int i = 0; i < IP6_SEGMENTS; i++) {
            if (shortAtSegment(high, low, i) == 0) {
                final int count = countContiguousZeroShortsFrom(high, low, i);
                if (count > 1 && count > zn - z0) {
                    z0 = i;
                    zn = i + count;
                }
            }
        }

        final int MAX = IP6_SEGMENTS * 4 + IP6_SEGMENTS - 1;
        char[] buf;

        int len = 0;
        if (z0 < 0) {
            buf = new char[MAX];
            len = appendHex(high, low, 0, IP6_SEGMENTS, buf, len);
        } else {
            int compacted = (zn - z0) * 4;
            buf = new char[MAX - compacted];
            len = appendHex(high, low, 0, z0, buf, len);
            len = Chars.append(buf, len, "::");
            len = appendHex(high, low, zn, IP6_SEGMENTS, buf, len);
        }

        return new String(buf, 0, len);
    }

    /**
     * The zero indexed short from the IPv6 string representation.
     *
     * @param high  the high bytes
     * @param low   the low bytes
     * @param index the short segment index
     * @return the short as a positive integer
     */
    private static int shortAtSegment(final long high, final long low, final int index) {
        // assert index >= 0 && index <= IP6_SEGMENTS;

        if (index < IP6_SEGMENTS / 2) {
            int shift = (3 - index) * Short.SIZE;
            return toShortInt(high >>> shift);
        }
        int shift = (7 - index) * Short.SIZE;
        return toShortInt(low >>> shift);
    }

    /**
     * Counts the number contiguous zeroes from a given offset in IPv6 string representation
     *
     * @param high   high bytes
     * @param low    low bytes
     * @param offset from
     * @return the number of zeroes
     */
    private static int countContiguousZeroShortsFrom(final long high, final long low, final int offset) {
        int count = 1;
        for (int i = offset + 1; i < IP6_SEGMENTS; i++) {
            if (!isZeroShort(high, low, i)) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Used in compact string notation.
     *
     * @param high  the high bytes
     * @param low   the low bytes
     * @param index the index of the short in an IPv6 string representation like "dead:beef:0000:0000:cafe:babe:0000:0000:cafe:babe" where 0 is "dead"
     * @return true if the component is zero
     */
    private static boolean isZeroShort(long high, long low, int index) {
        return shortAtSegment(high, low, index) == 0;
    }

    private static int appendHex(final long high, final long low, final int offset, final int max, final char[] buf, int blen) {
        int boff = blen;
        for (int i = offset; i < max; i++) {
            if (i != offset) {
                boff = Chars.append(buf, boff, ':');
            }
            final int shortSegment = shortAtSegment(high, low, i);
            boff = appendHex(buf, boff, shortSegment);
        }
        return boff;
    }

    private static int appendHex(final char[] buf, final int blen, final int shortVal) {
        final int NIBBLE_SIZE = 4;
        final int NIBBLE_MASK = 0xF;

        int offset = blen;
        if (shortVal == 0) {
            offset = Chars.append(buf, blen, '0');
        } else {
            for (int i = numberOfShortHexDigits(shortVal) - 1; i >= 0; i--) {
                int shift = i * NIBBLE_SIZE;
                int v = (shortVal >>> shift) & NIBBLE_MASK;
                char c = hex(v);
                offset = Chars.append(buf, offset, c);
            }
        }
        return offset;
    }

    private static char hex(int n) {
        return n < 10
                ? (char) (n + '0')
                : (char) ('a' + n - 10);
    }

    private static int numberOfShortHexDigits(final int n) {
        // assert n >= 0 && n <= 0xFFFF;
        if (n <= 0xF) {
            return 1;
        } else if (n <= 0xFF) {
            return 2;
        } else if (n <= 0xFFF) {
            return 3;
        }
        return 4;
    }

    private static int toShortInt(final long l) {
        return (int) (l & 0xFFFF);
    }

    static <T> T parse(CharSequence cs, V6Function<T> factory) {
        final int len = cs.length();
        validate(len >= 2, "Invalid string length", cs, ParseException::new);
        int split = Chars.indexOf(cs, "::");
        V6Segments.Iter head;
        V6Segments.Iter tail;
        if (split < 0) {
            head = V6Segments.forwards(cs, 0, cs.length());
            tail = V6Segments.forwards("", 0, 0);
        } else {
            head = V6Segments.forwards(cs, 0, split);
            tail = V6Segments.backwards(cs, split + 2, cs.length());
        }

        long highHead = 0;
        long lowHead = 0;
        int headSegs = 0;
        int shift = Short.SIZE * 3;
        while (headSegs < IP6_SEGMENTS / 2) {
            if (!head.hasNext()) {
                break;
            }
            headSegs++;
            long n = head.next() << shift;
            shift -= Short.SIZE;
            highHead |= n;
        }
        shift = Short.SIZE * 3;
        while (headSegs < IP6_SEGMENTS) {
            if (!head.hasNext()) {
                break;
            }
            headSegs++;
            long n = head.next() << shift;
            shift -= Short.SIZE;
            lowHead |= n;
        }

        long highTail = 0;
        long lowTail = 0;
        int tailSegs = 0;
        shift = 0;
        while (tailSegs < IP6_SEGMENTS / 2) {
            if (!tail.hasNext()) {
                break;
            }
            tailSegs++;
            long n = tail.next() << shift;
            shift += Short.SIZE;
            lowTail |= n;
        }
        shift = 0;
        while (tailSegs < IP6_SEGMENTS) {
            if (!tail.hasNext()) {
                break;
            }
            tailSegs++;
            long n = tail.next() << shift;
            shift += Short.SIZE;
            highTail |= n;
        }

        int segments = headSegs + tailSegs;
        if (split < 0) {
            validate(segments == IP6_SEGMENTS, "Invalid number of segments", cs, ParseException::new);
        } else {
            validate(segments < IP6_SEGMENTS, "Invalid number of segments", cs, ParseException::new);
        }
        validate(!head.hasNext(), "Invalid number of segments", cs, ParseException::new);
        validate(!tail.hasNext(), "Invalid number of segments", cs, ParseException::new);

        long high = highHead | highTail;
        long low = lowHead | lowTail;
        return factory.apply(high, low);
    }
}
