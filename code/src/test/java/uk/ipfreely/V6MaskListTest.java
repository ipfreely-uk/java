package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ipfreely.Family.v6;

public class V6MaskListTest {

  @Test
  public void testFirst() {
    assertEquals(v6().parse("::"), V6MaskList.MASKS.get(0));
  }

  @Test
  public void testLast() {
    assertEquals(v6().parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), V6MaskList.MASKS.get(V6MaskList.MASKS.size() - 1));
  }

  @Test
  public void testUnique() {
    assertEquals(V6MaskList.MASKS.size(), new HashSet<>(V6MaskList.MASKS).size());
  }
}
