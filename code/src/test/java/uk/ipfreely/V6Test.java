// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Block;
import uk.ipfreely.testing.EqualsTester;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class V6Test extends IpTests<V6> {

  private final Block<V6> net = AddressSets.block(v6().min(), 0);

  @Test
  public void testInternetSize() {
    BigInteger expected = BigInteger.valueOf(2).pow(128);
    assertEquals(expected, net.size());
  }

  @Test
  public void testMinAndMax() {
    testFeatures(net.first(), net.last());
  }

  @Test
  public void testArithmetic() {
    testArithmetic(v6(), Family.v6().parse(0, 1),
      Family.v6().parse(0xFL, 0xFL),
      Family.v6().parse(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL),
      Family.v6().parse(0xFFFFFFFFFFFFFFFFL, 0),
      Family.v6().parse(0, 0xFFFFFFFFFFFFFFFFL),
      Family.v6().parse(1, 0xFFFFFFFFFFFFFFFFL),
      Family.v6().parse(0xFFFFFFFFFFFFFFFFL, 1),
      Family.v6().parse(0, 0),
      Family.v6().parse(0, 2));
  }

  @Test
  public void testNextAndPrev() {
    V6 one = net.first().next();
    V6 zero = one.prev();

    assertEquals(BigInteger.ONE, one.toBigInteger());
    assertEquals(BigInteger.ZERO, zero.toBigInteger());
  }

  @Test
  public void testToString() {
    assertEquals("ffff:ffff:ffff::ffff:ffff:ffff", v6().parse("ffff:ffff:ffff::ffff:ffff:ffff").toString());
    assertEquals("dead:ffff:ffff::beef:ffff:ffff", v6().parse("DEAD:ffff:ffff:0:0:BEEF:ffff:ffff").toString());
    assertEquals("ffff:ffff:ffff:0:ffff:ffff:ffff:ffff", v6().parse("ffff:ffff:ffff:0:ffff:ffff:ffff:ffff").toString());
    assertEquals("ffff::ffff:ffff:0:0:ffff", v6().parse("ffff:0:0:ffff:ffff:0:0:ffff").toString());
    assertEquals("ffff:0:0:ffff:ffff::", v6().parse("ffff:0:0:ffff:ffff:0:0:0").toString());
    assertEquals("::", v6().parse("0:0:0:0:0:0:0:0").toString());

    for (String bad : new String[]{"foo.bar", "f:f:f", " ::", ":: ", "fffff::"}) {
      expect(bad, ParseException.class, () -> v6().parse(bad));
    }

    testToAndFrom(v6(), V6::toString, v6()::parse, this::isIp6);
  }

  private boolean isIp6(String ip) {
    try {
      Inet6Address.getByName(ip);
    } catch (UnknownHostException e) {
      return false;
    }
    return true;
  }

  @Test
  public void testBigInteger() {
    BigInteger minI = BigInteger.ZERO;
    BigInteger maxI = BigInteger.valueOf(2).pow(128).subtract(BigInteger.ONE);

    expect("-1", ParseException.class, () -> v6().parse(minI.subtract(BigInteger.ONE)));
    expect("+1", ParseException.class, () -> v6().parse(maxI.add(BigInteger.ONE)));

    testToAndFrom(v6(), V6::toBigInteger, v6()::parse, i -> i.compareTo(maxI) <= 0);
  }

  @Test
  public void testBytes() throws UnknownHostException {
    InetAddress addr = InetAddress.getByName("FE80::1");
    V6 ip = v6().parse(addr.getAddress());

    assertArrayEquals(addr.getAddress(), ip.toBytes());

    expect("", ParseException.class, v6()::parse);
    expect("", ParseException.class, () -> v6().parse((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));

    testToAndFrom(v6(), V6::toBytes, v6()::parse, b -> b.length == 16);
  }

  @Test
  public void testNot() {
    V6 address = v6().parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
    V6 expected = v6().parse("::");

    assertEquals(expected, address.not());
    assertEquals(address, address.not().not());
  }

  @Test
  public void testAnd() {
    V6 address = v6().parse("fe80:dead:ffff:0:0:ff:0:1");
    V6 mask = v6().parse("ffff:ffff:ffff::");
    V6 expected = v6().parse("fe80:dead:ffff::");

    assertEquals(expected, address.and(mask));
  }

  @Test
  public void testOr() {
    V6 address = v6().parse("fe80:dead:ffff:0:0:ff:0:1");
    V6 mask = v6().parse("ffff:0:ffff::");
    V6 expected = v6().parse("ffff:dead:ffff:0:0:ff:0:1");

    assertEquals(expected, address.or(mask));
  }

  @Test
  public void testInterned() {
    assertSame(v6().parse("::1"), v6().parse("::1"));
  }

  @Test
  public void testLeftShift() {
    assertEquals(v6().parse(0b10), v6().parse(1).shift(-1));
    assertEquals(v6().parse(0b100), v6().parse(1).shift(-2));
    assertEquals(v6().parse(0), v6().masks().get(1).shift(-1));
    assertEquals(v6().parse(1, 0), v6().parse(0, 0x80_00_00_00_00_00_00_00L).shift(-1));
    assertEquals(v6().parse(1, 0), v6().parse(1).shift(-64));
    assertEquals(v6().parse(0b10, 0), v6().parse(1).shift(-65));
  }

  @Test
  public void testRightShift() {
    assertEquals(v6().parse(0), v6().parse(1).shift(1));
    assertEquals(v6().parse(1), v6().parse(0b10).shift(1));
    assertEquals(v6().parse(1), v6().parse(1, 0).shift(Long.SIZE));
    assertEquals(v6().parse(2), v6().parse(1, 0).shift(63));
    assertEquals(v6().parse(1), v6().parse(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L, 0).shift(127));
    assertEquals(v6().parse(0, 0x80_00_00_00_00_00_00_00L), v6().parse(1, 0).shift(1));
    assertEquals(v6().parse(1, 0), v6().parse(0, 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L).shift(-1));
    assertEquals(v6().parse(0, 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L), v6().parse(1, 0).shift(1));
    assertEquals(v6().parse(1), v6().parse(1).shift(128));
    assertEquals(v6().parse(1).shift(-1), v6().parse(1).shift(-129));
  }

  @Test
  public void testNoShift() {
    V6 zero = v6().parse(0);
    V6 one = v6().parse(1);
    assertSame(zero, zero.shift(10));
    assertSame(one, one.shift(0));
  }

  @Test
  void equals() {
    EqualsTester.test(
            v6().parse(1),
            v6().max(),
            v6().min(),
            new Object()
    );
  }

  @Test
  void leadingZeros() {
    assertEquals(128, v6().min().leadingZeros());
    assertEquals(127, v6().parse(1).leadingZeros());
    assertEquals(126, v6().parse(2).leadingZeros());
    assertEquals(126, v6().parse(3).leadingZeros());
    assertEquals(0, v6().max().leadingZeros());
    byte[] addr = {0b01000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(1, v6().parse(addr).leadingZeros());
    byte[] addr2 = {0, 0, 0, 0, 0, 0, 0, 0, (byte) 0b10000000, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(64, v6().parse(addr2).leadingZeros());
    byte[] addr3 = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(63, v6().parse(addr3).leadingZeros());
  }

  @Test
  void trailingZeros() {
    assertEquals(128, v6().min().trailingZeros());
    assertEquals(0, v6().parse(1).trailingZeros());
    assertEquals(1, v6().parse(2).trailingZeros());
    assertEquals(0, v6().parse(3).trailingZeros());
    assertEquals(0, v6().max().trailingZeros());
    byte[] addr = {0b01000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(126, v6().parse(addr).trailingZeros());
    byte[] addr2 = {0, 0, 0, 0, 0, 0, 0, 0, (byte) 0b10000000, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(63, v6().parse(addr2).trailingZeros());
    byte[] addr3 = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(64, v6().parse(addr3).trailingZeros());
  }
}
