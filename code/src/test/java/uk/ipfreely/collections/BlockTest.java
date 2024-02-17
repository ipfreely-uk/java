package uk.ipfreely.collections;

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
    Block<V4> network = Ranges.block(ip, 24);

    assertEquals("192.168.12.0/24", network.cidrNotation());
  }

  @Test
  public void testFirstAndLast() {
    V4 ip = v4().parse("192.168.12.0");
    V4 broadcast = v4().parse("192.168.12.255");
    Block<V4> network = Ranges.block(ip, 24);

    assertEquals(broadcast, network.last());
    assertEquals(ip, network.first());
  }

  @Test
  public void testEquality() {
    Block<V6> network = Ranges.block(v6().parse("FE80:DEAD::0"), 32);
    Block<V6> other = Ranges.block(v6().parse("FE80:BEEF::0"), 32);

    assertEquals(network, Ranges.block(network.first(), 32));
    assertEquals(network.hashCode(), Ranges.block(network.first(), 32).hashCode());
    assertNotEquals(network, other);
  }

  @Test
  public void testFromString() {
    fromString(v4()::parse, "168.0.0.0", 24);
    fromString(v6()::parse, "fe80::", 128);
    fromString(v6()::parse, "fe80::", 16);
    fromString(v4()::parse, "168.0.0.1", 32);

    IpTests.expect("bad cidr", ParseException.class, () -> Ranges.parseCidr("168.0.0.1/24"));
    IpTests.expect("bad cidr", ParseException.class, () -> Ranges.parseCidr("fe80::/0"));
    IpTests.expect("bad cidr", ParseException.class, () -> Ranges.parseCidr("foobar"));
    IpTests.expect("bad cidr", ParseException.class, () -> Ranges.parseCidr("168.0.0.1//32"));
  }

  @Test
  public void testAddress() {
    Block<V6> fe80_1 = Ranges.parseCidr(Family.v6(), "fe80::1/128");
    assertInstanceOf(Block.class, fe80_1);
    Block<V6> localhost = Ranges.block(v6().parse("::1"), 128);
    assertInstanceOf(Block.class, localhost);
  }

  @Test
  void subnets() {
    Block<V4> internet = Ranges.block(v4().min(), v4().max());
    Block<V4> first = Ranges.block(v4().parse("0.0.0.0"), v4().parse("0.255.255.255"));
    Block<V4> last = Ranges.block(v4().parse("255.0.0.0"), v4().parse("255.255.255.255"));
    List<Block<V4>> actual = internet.subnets(8).collect(Collectors.toList());
    assertEquals(256, actual.size());
    assertEquals(first, actual.get(0));
    assertEquals(last, actual.get(actual.size() - 1));
  }

  @Test
  void subnetToIndividualAddresses() {
    Block<V4> internet = Ranges.block(v4().parse("192.168.0.0"), v4().parse("192.168.0.255"));
    long actual = internet.subnets(v4().bitWidth()).count();
    assertEquals(256, actual);
  }

  @Test
  void subnetItself() {
    Block<V4> internet = Ranges.block(v4().min(), v4().max());
    Optional<Block<V4>> actual = internet.subnets(internet.maskBits()).findAny();
    assertTrue(actual.isPresent());
    assertEquals(internet, actual.get());
  }

  private <I extends Address<I>> void fromString(Function<String, I> parser, String ip, int mask) {
    String cidr = ip + "/" + mask;
    Block<?> block = Ranges.parseCidr(cidr);
    assertEquals(parser.apply(ip), block.first());
    assertEquals(mask, block.maskBits());
  }
}
