package uk.ipfreely.examples;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ConvertTest {

    @Test
    void toInetAddress() {
        {
            Address<?> src = Family.v4().parse("127.0.0.1");
            InetAddress actual = Convert.toInetAddress(src);
            assertInstanceOf(Inet4Address.class, actual);
            assertArrayEquals(src.toBytes(), actual.getAddress());
        }
        {
            Address<?> src = Family.v6().parse("fe80::1");
            InetAddress actual = Convert.toInetAddress(src);
            assertInstanceOf(Inet6Address.class, actual);
            assertArrayEquals(src.toBytes(), actual.getAddress());
        }
    }

    @Test
    void toAddress() throws UnknownHostException {
        {
            InetAddress src = Inet4Address.getByName("127.0.0.1");
            Address<?> actual = Convert.toAddress(src);
            assertSame(Family.v4(), actual.family());
            assertArrayEquals(src.getAddress(), actual.toBytes());
        }
        {
            InetAddress src = Inet6Address.getByName("::1");
            Address<?> actual = Convert.toAddress(src);
            assertSame(Family.v6(), actual.family());
            assertArrayEquals(src.getAddress(), actual.toBytes());
        }
    }
}