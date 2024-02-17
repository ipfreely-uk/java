package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class V4MaskListTest {

  @Test
  public void testFirst() {
    assertEquals(Family.v4().parse("0.0.0.0"), V4MaskList.MASKS.get(0));
  }

  @Test
  public void testLast() {
    assertEquals(Family.v4().parse("255.255.255.255"), V4MaskList.MASKS.get(V4MaskList.MASKS.size() - 1));
  }

  @Test
  public void testUnique() {
    assertEquals(V4MaskList.MASKS.size(), new HashSet<>(V4MaskList.MASKS).size());
  }
}
