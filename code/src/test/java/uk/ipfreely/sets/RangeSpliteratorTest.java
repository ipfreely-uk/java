package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Address;
import uk.ipfreely.testing.SpliteratorTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.Family.v6;

public class RangeSpliteratorTest {

  private final Range<?>[] ranges = {
          AddressSets.range(v4().fromUint(0), v4().fromUint(0)),
          AddressSets.range(v4().fromUint(0), v4().fromUint(1)),
          AddressSets.range(v4().fromUint(0), v4().fromUint(2)),
          AddressSets.range(v4().fromUint(0), v4().fromUint(100)),
          AddressSets.range(v6().fromUint(0), v6().fromUint(100)),
          AddressSets.range(v4().parse("127.0.0.0"), v4().parse("127.0.0.255")),
  };

  @Test
  public void testTryAdvance() {
    for (Range<?> range : ranges) {
      List<?> ips = toList(range.spliterator());
      verify(range, ips);
    }
  }

  @Test
  void trySplit() {
    for (Range<?> range : ranges) {
      Spliterator<?> init = range.spliterator();
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
    for (Address<?> ipAddr : range) {
      assertTrue(ips.contains(ipAddr));
    }
    assertEquals(range.first(), ips.get(0));
    assertEquals(range.last(), ips.get(ips.size() - 1));
  }

  @Test
  void spliterator() {
    SpliteratorTester.test(ranges[0].spliterator());
  }
}
