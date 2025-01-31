// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v6;

public class V6MaskListTest {

  @Test
  public void testFirst() {
    assertEquals(v6().parse("::"), V6Masks.MASKS.get(0));
  }

  @Test
  public void testLast() {
    assertEquals(v6().parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), V6Masks.MASKS.get(V6Masks.MASKS.size() - 1));
  }

  @Test
  public void testUnique() {
    assertEquals(V6Masks.MASKS.size(), new HashSet<>(V6Masks.MASKS).size());
  }

  @Test
  public void testOutOfBounds() {
    assertThrows(IndexOutOfBoundsException.class, () -> V6Masks.MASKS.get(Consts.V6_WIDTH + 1));
  }

  @Test
  void testContains() {
    for (V6 a : V6Masks.MASKS) {
      assertTrue(V6Masks.MASKS.contains(a));
    }
    V6 two = Family.v6().parse(2);
    assertFalse(V6Masks.MASKS.contains(two));
    assertFalse(V6Masks.MASKS.contains(null));
  }

  @Test
  void testIndexOf() {
    for (int i = 0; i < V6Masks.MASKS.size(); i++) {
      V6 m = V6Masks.MASKS.get(i);
      int actual = V6Masks.MASKS.indexOf(m);
      assertEquals(i, actual);
    }
    V6 two = Family.v6().parse(2);
    assertEquals(-1, V6Masks.MASKS.indexOf(two));
    assertEquals(-1, V6Masks.MASKS.indexOf(null));
  }
}
