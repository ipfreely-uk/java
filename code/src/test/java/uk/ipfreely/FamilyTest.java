// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;
import uk.ipfreely.testing.Addresses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class FamilyTest {

  @Test
  public void testFamilies() {
    assertEquals(V4.class, Family.v4().type());
    assertEquals(V6.class, Family.v6().type());
  }

  @Test
  public void testParsing() {
    assertSame(V4.class, Family.unknown("127.0.0.1").getClass());
    assertSame(V6.class, Family.unknown("::").getClass());
    assertSame(V6.class, Family.unknown("A::").getClass());
    assertSame(V6.class, Family.unknown("a::").getClass());

    IpTests.expect("Not an IP address test", ParseException.class, () -> Family.unknown("foobar"));
    IpTests.expect("Not an IP address test", ParseException.class, () -> Family.unknown("z"));
    IpTests.expect("Not an IP address test", ParseException.class, () -> Family.unknown(" "));

    for(String addr : Addresses.valid(Family.v4())) {
      assertSame(V4.class, Family.unknown(addr).getClass());
    }
    for(String addr : Addresses.valid(Family.v6())) {
      assertSame(V6.class, Family.unknown(addr).getClass());
    }

    for(String addr : Addresses.invalid(Family.v4())) {
      assertThrowsExactly(ParseException.class, () -> v4().parse(addr), addr);
    }
    for(String addr : Addresses.invalid(Family.v6())) {
      assertThrowsExactly(ParseException.class, () -> v6().parse(addr), addr);
    }
  }

  @Test
  public void testFromBytes() {
    V4 ip4 = Family.v4().parse("127.1.2.3");
    assertEquals(ip4, Family.unknown(ip4.toBytes()));
    V6 ip6 = v6().parse("fe80::dead:1");
    assertEquals(ip6, Family.unknown(ip6.toBytes()));
  }

  @Test
  void fromLongs() {
    V4 ip4 = Family.v4().parse(0L, 0xFFFFFFFFL);
    assertEquals(0L, ip4.highBits());
    assertEquals(0xFFFFFFFFL, ip4.lowBits());
    V6 ip6 = v6().parse(0xabc, 0xdef);
    assertEquals(0xabc, ip6.highBits());
    assertEquals(0xdef, ip6.lowBits());
    assertThrowsExactly(ParseException.class, () -> v4().parse(1, 0));
    assertThrowsExactly(ParseException.class, () -> v4().parse(0, 0xFFFFFFFFL + 1));
    assertThrowsExactly(ParseException.class, () -> v4().parse(0, -1));
  }

  @Test
  void subnets() {
    assertEquals(Family.v4().subnets().toString(), Family.v4().subnets().toString());
    assertSame(Family.v4(), Family.v4().subnets().family());
  }
}
