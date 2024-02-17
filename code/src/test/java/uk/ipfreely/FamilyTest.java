package uk.ipfreely;

import org.junit.jupiter.api.Test;
import uk.ipfreely.testing.Addresses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v6;

public class FamilyTest {

  @Test
  public void testFamilies() {
    assertEquals(V4.class, Family.v4().ipType());
    assertEquals(V6.class, Family.v6().ipType());
  }

  @Test
  public void testParsing() {
    assertSame(V4.class, Family.parseUnknown("127.0.0.1").getClass());
    assertSame(V6.class, Family.parseUnknown("::").getClass());
    assertSame(V6.class, Family.parseUnknown("A::").getClass());
    assertSame(V6.class, Family.parseUnknown("a::").getClass());

    IpTests.expect("Not an IP address test", ParseException.class, () -> Family.parseUnknown("foobar"));
    IpTests.expect("Not an IP address test", ParseException.class, () -> Family.parseUnknown("z"));
  }

  @Test
  public void testFromBytes() {
    V4 ip4 = Family.v4().parse("127.1.2.3");
    assertEquals(ip4, Family.parseUnknown(ip4.toBytes()));
    V6 ip6 = v6().parse("fe80::dead:1");
    assertEquals(ip6, Family.parseUnknown(ip6.toBytes()));
  }

  @Test
  void regexV6() {
    testRegex(Family.v6());
  }

  @Test
  void regexV4() {
    testRegex(Family.v6());
  }

  private void testRegex(Family<?> family) {
    Pattern p = Pattern.compile("^" + family.regex() + "$");

    for (String candidate : Addresses.valid(family)) {
      Matcher m = p.matcher(candidate);
      assertTrue(m.matches());
    }
  }
}
