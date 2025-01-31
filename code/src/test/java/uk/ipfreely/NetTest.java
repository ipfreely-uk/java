package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class NetTest {

    @Test
    void inetAddress() {
        Addr<?> expected = Family.v4().parse("127.0.0.1");
        InetAddress ia = Net.toInetAddress(expected);
        Addr<?> actual = Net.toAddr(ia);
        assertEquals(expected, actual);

        assertThrowsExactly(AssertionError.class, () -> Net.toInetAddress(null));
    }

    @Test
    void toInet4Address() {
        V4 expected = Family.v4().parse("127.0.0.1");
        Inet4Address ia = Net.toInet4Address(expected);
        V4 actual = Net.toV4(ia);
        assertEquals(expected, actual);
    }

    @Test
    void toInet6Address() {
        V6 expected = Family.v6().parse("fe80::1");
        Inet6Address ia = Net.toInet6Address(expected);
        V6 actual = Net.toV6(ia);
        assertEquals(expected, actual);
    }
}