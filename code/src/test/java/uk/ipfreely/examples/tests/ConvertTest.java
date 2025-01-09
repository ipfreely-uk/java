// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.examples.tests;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.examples.Convert;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ConvertTest {

    @Test
    void toInetAddress() {
        {
            Addr<?> src = Family.v4().parse("127.0.0.1");
            InetAddress actual = Convert.toInetAddress(src);
            assertInstanceOf(Inet4Address.class, actual);
            assertArrayEquals(src.toBytes(), actual.getAddress());
        }
        {
            Addr<?> src = Family.v6().parse("fe80::1");
            InetAddress actual = Convert.toInetAddress(src);
            assertInstanceOf(Inet6Address.class, actual);
            assertArrayEquals(src.toBytes(), actual.getAddress());
        }
    }

    @Test
    void toAddress() throws UnknownHostException {
        {
            InetAddress src = Inet4Address.getByName("127.0.0.1");
            Addr<?> actual = Convert.toAddress(src);
            assertSame(Family.v4(), actual.family());
            assertArrayEquals(src.getAddress(), actual.toBytes());
        }
        {
            InetAddress src = Inet6Address.getByName("::1");
            Addr<?> actual = Convert.toAddress(src);
            assertSame(Family.v6(), actual.family());
            assertArrayEquals(src.getAddress(), actual.toBytes());
        }
    }
}