// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Addr;
import uk.ipfreely.V6;
import uk.ipfreely.testing.SpliteratorTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class RangeSpliteratorTest {

  private final Range<?>[] ranges = {
          AddressSets.range(v4().parse(0), v4().parse(0)),
          AddressSets.range(v4().parse(0), v4().parse(1)),
          AddressSets.range(v4().parse(0), v4().parse(2)),
          AddressSets.range(v4().parse(0), v4().parse(100)),
          AddressSets.range(v6().parse(0), v6().parse(100)),
          AddressSets.range(v4().parse("127.0.0.0"), v4().parse("127.0.0.255")),
          AddressSets.range(v6().min(), v6().max()),
          AddressSets.range(v6().min(), v6().max().divide(v6().parse(2))),
  };

  @Test
  public void testTryAdvance() {
    for (Range<?> range : ranges) {
      Spliterator<?> spliterator = range.spliterator();
      if (spliterator.estimateSize() > 10_000) {
        continue;
      }
      List<?> ips = toList(spliterator);
      verify(range, ips);
    }
  }

  @Test
  void trySplit() {
    for (Range<?> range : ranges) {
      Spliterator<?> init = range.spliterator();
      if (init.estimateSize() > 10_000) {
        continue;
      }
      Spliterator<?> prefix = init.trySplit();
      if (range.first().equals(range.last())) {
        assertNull(prefix);
        continue;
      }
      List<?> ips = toList(prefix, init);
      verify(range, ips);
    }
  }

  private List<Object> toList(Spliterator<?>...spliterators) {
    List<Object> ips = new ArrayList<>();
    for (Spliterator<?> sp : spliterators) {
      sp.forEachRemaining(ips::add);
    }
    return ips;
  }

  private void verify(Range<?> range, List<?> ips) {
    int len = range.size().intValue();
    assertEquals(len, ips.size());
    for (Addr<?> ipAddr : range) {
      assertTrue(ips.contains(ipAddr));
    }
    assertEquals(range.first(), ips.get(0));
    assertEquals(range.last(), ips.get(ips.size() - 1));
  }

  @Test
  void spliterator() {
    SpliteratorTester.test(ranges[0].spliterator());
  }

  @Test
  void estimateSize() {
    {
      var splitter = new RangeSpliterator<>(v6().min(), v6().min());
      long actual = splitter.estimateSize();
      assertEquals(1, actual);
    }
    {
      var splitter = new RangeSpliterator<>(v6().min(), v6().max());
      long actual = splitter.estimateSize();
      assertEquals(Long.MAX_VALUE, actual);
    }
    {
      V6 value = v6().parse(0, Long.MAX_VALUE);
      var splitter = new RangeSpliterator<>(v6().min(), value);
      long actual = splitter.estimateSize();
      assertEquals(Long.MAX_VALUE, actual);
    }
    {
      V6 value = v6().parse(0, Long.MAX_VALUE - 1);
      var splitter = new RangeSpliterator<>(v6().min(), value);
      long actual = splitter.estimateSize();
      assertEquals(Long.MAX_VALUE, actual);
    }
    {
      V6 value = v6().parse(0, Long.MAX_VALUE - 2);
      var splitter = new RangeSpliterator<>(v6().min(), value);
      long actual = splitter.estimateSize();
      assertEquals(Long.MAX_VALUE - 1, actual);
    }
    {
      V6 value = v6().parse(0, Long.MAX_VALUE - 2);
      var splitter = new RangeSpliterator<>(v6().min(), value);
      long actual = splitter.estimateSize();
      assertEquals(Long.MAX_VALUE - 1, actual);
    }
    {
      var splitter = new RangeSpliterator<>(v4().min(), v4().max());
      long actual = splitter.estimateSize();
      assertEquals(0xFFFFFFFFL + 1, actual);
    }
  }
}
