package uk.ipfreely;

import java.util.NoSuchElementException;

import static uk.ipfreely.Validation.validate;

final class V6Segments {
    private static final Iter EMPTY = new Empty();

    private V6Segments() {}

    public static Iter forwards(CharSequence cs, int offset, int limit) {
        class Forward extends Iter {
            private int pos = offset;

            @Override
            public boolean hasNext() {
                return pos < limit;
            }

            @Override
            public long advance() {
                int r = hexDigit(cs.charAt(pos++));
                int count = 1;
                while (pos < limit) {
                    char ch = cs.charAt(pos++);
                    if (ch == ':') {
                        break;
                    }
                    r <<= 4;
                    r += hexDigit(ch);
                    count++;
                    validate(count <= 4, "Exceeded short range", r, ParseException::new);
                }
                return r;
            }
        }

        return limit - offset == 0
                ? EMPTY
                : new Forward();
    }

    public static Iter backwards(CharSequence cs, int offset, int limit) {
        class Backwards extends Iter {
            private int pos = limit;

            @Override
            public boolean hasNext() {
                return pos > offset;
            }

            @Override
            public long advance() {
                int r = hexDigit(cs.charAt(--pos));
                int shift = 4;
                int count = 1;
                while (pos > offset) {
                    char ch = cs.charAt(--pos);
                    if (ch == ':') {
                        break;
                    }
                    int d = hexDigit(ch) << shift;
                    r += d;
                    shift += 4;
                    count++;
                    validate(count <= 4, "Exceeded short range", r, ParseException::new);
                }
                return r;
            }
        }

        return limit - offset == 0
                ? EMPTY
                : new Backwards();
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xA;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xA;
        }
        throw new ParseException("Invalid character " + c);
    }

    static abstract class Iter {
        abstract boolean hasNext();
        final long next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return advance();
        }
        abstract long advance();
    }

    private static final class Empty extends Iter {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public long advance() {
            return 0;
        }
    }
}
