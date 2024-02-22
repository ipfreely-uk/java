package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.testing.EqualsTester;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class RangeTest {

  private V6 first = Family.v6().parse("fe80::");
  private V6 last = Family.v6().parse("fe80::a");

  @Test
  public void testWrongOrder() {
    assertThrowsExactly(IllegalArgumentException.class, () ->{
      AddressSets.range(last, first);
    });
  }

  @Test
  public void testSize() {
    BigInteger size = AddressSets.range(first, last).size();
    assertEquals(BigInteger.valueOf(11), size);
  }

  @Test
  public void testStream() {
    long size = AddressSets.range(first, last).stream().parallel().count();
    assertEquals(11, size);
  }

  @Test
  public void testNoSuchElement() {
    assertThrowsExactly(NoSuchElementException.class, ()-> {
      Iterator<?> i = AddressSets.range(first, first).iterator();
      i.next();
      i.next();
    });
  }

  @Test
  public void testToString() {
    assertEquals(first + "-" + last, AddressSets.range(first, last).toString());
  }

  @Test
  public void testEquality() {
    assertEquals(AddressSets.range(first, last), AddressSets.range(first, last));
    assertEquals(AddressSets.range(first, last).hashCode(), AddressSets.range(first, last).hashCode());

    Range<?> r = AddressSets.range(first, last);
    assertEquals(first.hashCode() * 31 + last.hashCode(), r.hashCode());
    assertTrue(Objects.equals(r, r));
    assertFalse(r.equals(null));
    assertFalse(r.equals(new Object()));
  }

  @Test
  public void testContains() {
    Range<V6> range = AddressSets.range(first, last);
    assertTrue(range.contains(first));
    assertTrue(range.contains(last));
    assertTrue(range.contains(first.next()));
    assertTrue(range.contains(last.prev()));
    assertFalse(range.contains(last.next()));
    assertFalse(range.contains(v4().parse("127.0.0.1")));
  }

  @Test
  public void testIntersects() {
    Range<V6> range = AddressSets.range(first, last);
    assertTrue(range.intersects(range));
    assertTrue(range.intersects(AddressSets.range(first.next(), last.next())));
  }

  @Test
  void intersection() {
    Family<V4> v4 = v4();
    V4 ten = v4.fromUint(10);
    V4 eleven = v4.fromUint(11);
    V4 fourteen = v4.fromUint(14);
    V4 fifteen = v4.fromUint(15);

    {
      Range<V4> r = AddressSets.range(ten, fifteen);
      Range<V4> actual = intersect(r, r);
      assertSame(r, actual);
    }
    {
      Range<V4> superset = AddressSets.range(ten, fifteen);
      Range<V4> subset = AddressSets.range(eleven, fourteen);
      Range<V4> actual = intersect(superset, subset);
      assertSame(subset, actual);
    }
    {
      Range<V4> r1 = AddressSets.range(ten, fourteen);
      Range<V4> r2 = AddressSets.range(eleven, fifteen);
      Range<V4> actual = intersect(r1, r2);
      Range<V4> expected = AddressSets.range(eleven, fourteen);
      assertEquals(expected, actual);
    }
    {
      Range<V4> r1 = AddressSets.range(ten, eleven);
      Range<V4> r2 = AddressSets.range(fourteen, fifteen);
      Range<V4> actual = intersect(r1, r2);
      assertNull(actual);
    }
  }

  private Range<V4> intersect(Range<V4> r1, Range<V4> r2) {
    Optional<Range<V4>> o1 = r1.intersection(r2);
    Optional<Range<V4>> o2 = r2.intersection(r1);
    assertEquals(o1.isPresent(), o2.isPresent());
    if (o1.isPresent() && o2.isPresent()) {
      Range<V4> a1 = o1.get();
      Range<V4> a2 = o2.get();
      assertEquals(a1, a2);
      return a1;
    }
    return null;
  }

  @Test
  public void testBlock() {
    Range<V4> v4 = AddressSets.range(v4().parse("168.10.10.0"), v4().parse("168.10.10.255"));
    Range<V6> v6 = AddressSets.range(v6().parse("fe80::"), v6().parse("fe80::FFFF:FFFF"));
    assertInstanceOf(Block.class, v4);
    assertInstanceOf(Block.class, v6);
  }

  @Test
  public void testEquals() {
    Object[] objects = {
            new Object(),
            AddressSets.range(v4().parse("168.10.10.0"), v4().parse("168.10.10.255")),
            AddressSets.range(v6().parse("fe80::"), v6().parse("fe80::FFFF:FFFF")),
            AddressSets.range(v4().parse("168.10.10.0"), v4().parse("168.10.10.255")),
            AddressSets.range(v6().parse("fe80::"), v6().parse("fe80::FFFF:FFFF")),
            AddressSets.range(v4().parse("168.10.10.0"), v4().parse("168.10.10.255")),
            AddressSets.range(v6().parse("fe80::"), v6().parse("fe80::FFFF:FFFF")),
    };

    EqualsTester.test(objects);
  }

  @Test
  public void testFailingBlock() {
    V4 start = v4().parse("224.0.2.7");
    V4 end = v4().parse("224.0.2.8");
    AddressSets.range(start, end);
  }

  @Test
  void blocks() {
    Block<V4> b = AddressSets.parseCidr(Family.v4(), "192.168.0.0/24");
    {
      // itself
      assertEquals(1L, b.blocks().count());
      assertEquals(singletonList(b), b.blocks().collect(Collectors.toList()));
    }
    {
      // extra address
      Range<V4> r = AddressSets.range(b.first().prev(), b.last());
      List<Block<V4>> blocks = r.blocks().collect(Collectors.toList());
      assertEquals(2, blocks.size());
    }
  }

  @Test
  void combine() {
    V4 zero = Family.v4().fromUint(0);
    V4 one = Family.v4().fromUint(1);
    V4 ten = Family.v4().fromUint(10);
    {
      // combined with self
      Range<V4> r = AddressSets.range(zero, zero);
      Optional<Range<V4>> opt = AddressSets.combine(r, r);
      assertTrue(opt.isPresent());
      assertEquals(r, opt.get());
    }
    {
      // combined, adjacent
      Range<V4> r0 = AddressSets.range(zero, zero);
      Range<V4> r1 = AddressSets.range(one, one);
      Range<V4> expected = AddressSets.range(zero, one);
      Optional<Range<V4>> opt = AddressSets.combine(r0, r1);
      assertTrue(opt.isPresent());
      assertEquals(expected, opt.get());
    }
    {
      // combined, adjacent, reversed
      Range<V4> r0 = AddressSets.range(zero, zero);
      Range<V4> r1 = AddressSets.range(one, one);
      Range<V4> expected = AddressSets.range(zero, one);
      Optional<Range<V4>> opt = AddressSets.combine(r1, r0);
      assertTrue(opt.isPresent());
      assertEquals(expected, opt.get());
    }
    {
      // combined, superset
      Range<V4> superset = AddressSets.range(zero, ten);
      Range<V4> subset = AddressSets.range(one, one);
      Range<V4> expected = AddressSets.range(zero, ten);
      Optional<Range<V4>> opt = AddressSets.combine(subset, superset);
      assertTrue(opt.isPresent());
      assertEquals(expected, opt.get());
    }
    {
      // combined, superset, reversed
      Range<V4> superset = AddressSets.range(zero, ten);
      Range<V4> subset = AddressSets.range(one, one);
      Range<V4> expected = AddressSets.range(zero, ten);
      Optional<Range<V4>> opt = AddressSets.combine(superset, subset);
      assertTrue(opt.isPresent());
      assertEquals(expected, opt.get());
    }
    {
      // not adjacent
      Range<V4> r0 = AddressSets.range(zero, zero);
      Range<V4> r1 = AddressSets.range(ten, ten);
      Optional<Range<V4>> opt = AddressSets.combine(r0, r1);
      assertFalse(opt.isPresent());
    }
    {
      // no overflow
      V4 max = Family.v4().max();
      Range<V4> r0 = AddressSets.range(zero, zero);
      Range<V4> r1 = AddressSets.range(max, max);
      Optional<Range<V4>> opt = AddressSets.combine(r0, r1);
      assertFalse(opt.isPresent());
    }
  }
}
