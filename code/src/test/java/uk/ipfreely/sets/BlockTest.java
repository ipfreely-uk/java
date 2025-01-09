// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class BlockTest {
  
  @Test
  public void testCidrToString() {
    V4 ip = v4().parse("192.168.12.0");
    Block<V4> network = AddressSets.block(ip, 24);

    assertEquals("192.168.12.0/24", network.cidrNotation());
  }

  @Test
  public void testFirstAndLast() {
    V4 ip = v4().parse("192.168.12.0");
    V4 broadcast = v4().parse("192.168.12.255");
    Block<V4> network = AddressSets.block(ip, 24);

    assertEquals(broadcast, network.last());
    assertEquals(ip, network.first());
  }

  @Test
  public void testEquality() {
    Block<V6> network = AddressSets.block(v6().parse("FE80:DEAD::0"), 32);
    Block<V6> other = AddressSets.block(v6().parse("FE80:BEEF::0"), 32);

    assertEquals(network, AddressSets.block(network.first(), 32));
    assertEquals(network.hashCode(), AddressSets.block(network.first(), 32).hashCode());
    assertNotEquals(network, other);
  }

  @Test
  public void testFromString() {
    fromString(v4()::parse, "168.0.0.0", 24);
    fromString(v6()::parse, "fe80::", 128);
    fromString(v6()::parse, "fe80::", 16);
    fromString(v4()::parse, "168.0.0.1", 32);

    IpTests.expect("bad cidr", ParseException.class, () -> AddressSets.parseCidr("168.0.0.1/24"));
    IpTests.expect("bad cidr", ParseException.class, () -> AddressSets.parseCidr("fe80::/0"));
    IpTests.expect("bad cidr", ParseException.class, () -> AddressSets.parseCidr("foobar"));
    IpTests.expect("bad cidr", ParseException.class, () -> AddressSets.parseCidr("168.0.0.1//32"));
  }

  @Test
  public void testAddress() {
    Block<V6> fe80_1 = AddressSets.parseCidr(Family.v6(), "fe80::1/128");
    assertInstanceOf(Block.class, fe80_1);
    Block<V6> localhost = AddressSets.block(v6().parse("::1"), 128);
    assertInstanceOf(Block.class, localhost);

    IpTests.expect("bad cidr", ParseException.class, () -> AddressSets.parseCidr(v6(), "168.0.0.1/32"));
  }

  @Test
  void subnets() {
    Block<V4> internet = AddressSets.block(v4().min(), v4().max());
    Block<V4> first = AddressSets.block(v4().parse("0.0.0.0"), v4().parse("0.255.255.255"));
    Block<V4> last = AddressSets.block(v4().parse("255.0.0.0"), v4().parse("255.255.255.255"));
    List<Block<V4>> actual = internet.subnets(8).collect(Collectors.toList());
    assertEquals(256, actual.size());
    assertEquals(first, actual.get(0));
    assertEquals(last, actual.get(actual.size() - 1));

    assertThrowsExactly(IllegalArgumentException.class, () -> first.subnets(0));
    assertThrowsExactly(IllegalArgumentException.class, () -> first.subnets(100));
  }

  @Test
  void subnetToIndividualAddresses() {
    Block<V4> internet = AddressSets.block(v4().parse("192.168.0.0"), v4().parse("192.168.0.255"));
    long actual = internet.subnets(v4().width()).count();
    assertEquals(256, actual);
  }

  @Test
  void subnetItself() {
    Block<V4> internet = AddressSets.block(v4().min(), v4().max());
    Optional<Block<V4>> actual = internet.subnets(internet.maskSize()).findAny();
    assertTrue(actual.isPresent());
    assertEquals(internet, actual.get());
  }

  private <I extends Addr<I>> void fromString(Function<String, I> parser, String ip, int mask) {
    String cidr = ip + "/" + mask;
    Block<?> block = AddressSets.parseCidr(cidr);
    assertEquals(parser.apply(ip), block.first());
    assertEquals(mask, block.maskSize());
  }

  @Test
  void badMask() {
    assertThrowsExactly(IllegalArgumentException.class, () -> AddressSets.block(v6().min(), -1));
    assertThrowsExactly(IllegalArgumentException.class, () -> AddressSets.block(v6().min(), 129));
    assertThrowsExactly(IllegalArgumentException.class, () -> AddressSets.block(v4().min(), 33));
    assertThrowsExactly(IllegalArgumentException.class, () -> AddressSets.block(v4().max(), 0));
  }
}
