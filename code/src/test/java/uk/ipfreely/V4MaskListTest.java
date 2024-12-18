// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class V4MaskListTest {

  @Test
  public void testFirst() {
    assertEquals(Family.v4().parse("0.0.0.0"), V4Masks.MASKS.get(0));
  }

  @Test
  public void testLast() {
    assertEquals(Family.v4().parse("255.255.255.255"), V4Masks.MASKS.get(V4Masks.MASKS.size() - 1));
  }

  @Test
  public void testUnique() {
    assertEquals(V4Masks.MASKS.size(), new HashSet<>(V4Masks.MASKS).size());
  }

  @Test
  public void testOutOfBounds() {
    assertThrows(IndexOutOfBoundsException.class, () -> V4Masks.MASKS.get(Consts.V4_WIDTH + 1));
  }
}
