// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Block;
import uk.ipfreely.testing.EqualsTester;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;

public class V4Test extends IpTests<V4> {

  private final Block<V4> net = AddressSets.block(v4().min(), 0);

  @Test
  public void testInternetSize() {
    BigInteger expected = BigInteger.valueOf(2).pow(32);
    assertEquals(expected, net.size());
  }

  @Test
  public void testMinAndMax() {
    testFeatures(net.first(), net.last());
  }

  @Test
  public void testArithmetic() {
    testArithmetic(v4(), v4().parse(1),
      v4().parse(0xFFFFFFFF),
      v4().parse(0xFFFF),
      v4().parse(0x1FFFF),
      v4().parse(0));
  }

  @Test
  public void testNextAndPrev() {
    V4 one = net.first().next();
    V4 zero = one.prev();

    assertEquals(BigInteger.ONE, one.toBigInteger());
    assertEquals(BigInteger.ZERO, zero.toBigInteger());
  }

  @Test
  public void testToString() {
    assertEquals("127.0.0.1", v4().parse("127.0.0.1").toString());
    assertEquals("0.0.0.0", net.first().toString());
    assertEquals("255.255.255.255", net.last().toString());

    for (String bad : new String[]{"foo.bar", "255.0.256.1", " 255.0.255.1", "255.0.255.1 ", "2000.0.255.1"}) {
      expect(bad, ParseException.class, () -> v4().parse(bad));
    }

    testToAndFrom(v4(), V4::toString, v4()::parse, ip -> ip.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$"));
  }

  @Test
  public void testBigInteger() {
    BigInteger minI = BigInteger.ZERO;
    BigInteger maxI = BigInteger.valueOf(0xFFFFFFFFL);

    assertEquals(v4().parse("0.0.0.0"), v4().parse(minI));
    assertEquals(v4().parse("255.255.255.255"), v4().parse(maxI));

    expect("", ParseException.class, () -> v4().parse(minI.subtract(BigInteger.ONE)));
    expect("", ParseException.class, () -> v4().parse(maxI.add(BigInteger.ONE)));
    testToAndFrom(v4(), V4::toBigInteger, v4()::parse, i -> i.compareTo(maxI) <= 0);
  }

  @Test
  public void testBytes() throws UnknownHostException {
    byte[] bytes = new byte[]{(byte) 255, (byte) 128, 0, 1};
    InetAddress addr = InetAddress.getByAddress(bytes);
    V4 ip = v4().parse(bytes);

    assertEquals(addr.getHostAddress(), ip.toString());
    assertArrayEquals(addr.getAddress(), ip.toBytes());

    expect("", ParseException.class, v4()::parse);
    expect("", ParseException.class, () -> v4().parse((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));

    testToAndFrom(v4(), V4::toBytes, v4()::parse, b -> b.length == 4);
  }

  @Test
  public void testNot() {
    byte[] address = new byte[]{(byte) 0xFF, 0b01010101, (byte) 0b10101010, 0};
    byte[] inverse = new byte[]{0x00, (byte) 0b10101010, 0b01010101, (byte) 0xFF};

    assertEquals(v4().parse(inverse), v4().parse(address).not());
  }

  @Test
  public void testAnd() {
    V4 address = v4().parse("192.168.12.1");
    V4 mask = v4().parse("255.255.255.0");
    V4 expected = v4().parse("192.168.12.0");

    assertEquals(expected, address.and(mask));
  }

  @Test
  public void testOr() {
    V4 address = v4().parse("192.168.12.1");
    V4 mask = v4().parse("255.255.255.0");
    V4 expected = v4().parse("255.255.255.1");

    assertEquals(expected, address.or(mask));
  }

  @Test
  public void testInterned() {
    assertSame(v4().parse("127.0.0.1"), v4().parse("127.0.0.1"));
  }

  @Test
  public void testLeftShift() {
    assertEquals(v4().parse(0b10), v4().parse(1).shift(-1));
    assertEquals(v4().parse(0b100), v4().parse(1).shift(-2));
    assertEquals(v4().parse(0), v4().parse(0b10000000_00000000_00000000_00000000).shift(-1));
    assertEquals(v4().parse(1), v4().parse(1).shift(32));
    assertEquals(v4().parse(1), v4().parse(0b10).shift(33));
    assertEquals(v4().parse(1), v4().parse(1).shift(-32));
  }

  @Test
  public void testRightShift() {
    assertEquals(v4().parse(0), v4().parse(1).shift(1));
    assertEquals(v4().parse(1), v4().parse(0b10).shift(1));
  }

  @Test
  public void testNoShift() {
    V4 zero = v4().parse(0);
    V4 one = v4().parse(1);
    assertSame(zero, zero.shift(10));
    assertSame(one, one.shift(0));
  }

  @Test
  public void testBits() {
    int expected = 0xFFFFFFFF;
    V4 ip = v4().parse(expected);
    assertEquals(expected, (int) ip.lowBits());
    assertEquals(0L, ip.highBits());
  }

  @Test
  void equals() {
    EqualsTester.test(
            v4().parse(1),
            v4().max(),
            v4().min(),
            new Object()
    );
  }

  @Test
  void leadingZeros() {
    assertEquals(32, v4().min().leadingZeros());
    assertEquals(31, v4().parse(1).leadingZeros());
    assertEquals(30, v4().parse(2).leadingZeros());
    assertEquals(30, v4().parse(3).leadingZeros());
    assertEquals(0, v4().max().leadingZeros());
    assertEquals(1, v4().parse(0b01000000_00000000_00000000_00000000).leadingZeros());
  }

  @Test
  void TrailingZeros() {
    assertEquals(32, v4().min().trailingZeros());
    assertEquals(0, v4().parse(1).trailingZeros());
    assertEquals(1, v4().parse(2).trailingZeros());
    assertEquals(0, v4().parse(3).trailingZeros());
    assertEquals(0, v4().max().trailingZeros());
    assertEquals(6, v4().parse(0b01000000).trailingZeros());
  }
}
