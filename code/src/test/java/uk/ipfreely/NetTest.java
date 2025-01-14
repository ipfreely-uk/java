package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class NetTest {

    @Test
    void inetAddress() {
        Addr<?> expected = Family.v4().parse("127.0.0.1");
        InetAddress ia = Net.toInetAddress(expected);
        Addr<?> actual = Net.toAddr(ia);
        assertEquals(expected, actual);

        assertThrowsExactly(AssertionError.class, () -> Net.toInetAddress(new ArtificialAddr()));
    }

    @Test
    void inet4Address() {
        V4 expected = Family.v4().parse("127.0.0.1");
        Inet4Address ia = Net.toInet4Address(expected);
        V4 actual = Net.toV4(ia);
        assertEquals(expected, actual);
    }

    @Test
    void inet6Address() {
        V6 expected = Family.v6().parse("fe80::1");
        Inet6Address ia = Net.toInet6Address(expected);
        V6 actual = Net.toV6(ia);
        assertEquals(expected, actual);
    }

    private static void throwChecked(Throwable t) {
        NetTest.<RuntimeException>throwIt(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwIt(Throwable t) throws T {
        throw (T) t;
    }

    private static class ArtificialAddr extends Addr<ArtificialAddr> {

        @Override
        public Family<ArtificialAddr> family() {
            return null;
        }

        @Override
        public boolean equals(Object other) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public BigInteger toBigInteger() {
            return null;
        }

        @Override
        public byte[] toBytes() {
            throwChecked(new UnknownHostException(""));
            return null;
        }

        @Override
        public long highBits() {
            return 0;
        }

        @Override
        public long lowBits() {
            return 0;
        }

        @Override
        public int leadingZeros() {
            return 0;
        }

        @Override
        public int trailingZeros() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public ArtificialAddr add(ArtificialAddr addend) {
            return null;
        }

        @Override
        public ArtificialAddr subtract(ArtificialAddr subtrahend) {
            return null;
        }

        @Override
        public ArtificialAddr multiply(ArtificialAddr multiplicand) {
            return null;
        }

        @Override
        public ArtificialAddr divide(ArtificialAddr denominator) {
            return null;
        }

        @Override
        public ArtificialAddr mod(ArtificialAddr denominator) {
            return null;
        }

        @Override
        public ArtificialAddr and(ArtificialAddr operand) {
            return null;
        }

        @Override
        public ArtificialAddr or(ArtificialAddr operand) {
            return null;
        }

        @Override
        public ArtificialAddr xor(ArtificialAddr operand) {
            return null;
        }

        @Override
        public ArtificialAddr not() {
            return null;
        }

        @Override
        public ArtificialAddr shift(int bits) {
            return null;
        }

        @Override
        public int compareTo(ArtificialAddr o) {
            return 0;
        }
    }
}