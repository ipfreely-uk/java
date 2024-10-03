// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IpMathTest {

  @Test
  public void testUint128BigInt() {
    bigInt(new UI128(0, 0), BigInteger.ZERO);
    bigInt(new UI128(0, 0x1A), BigInteger.valueOf(0x1A));
    bigInt(new UI128(0, 0xFFF), BigInteger.valueOf(0xFFF));
    bigInt(new UI128(0, 0xFFFF), BigInteger.valueOf(0xFFFF));
    bigInt(new UI128(0, 0xFFFFFF), BigInteger.valueOf(0xFFFFFF));
    bigInt(new UI128(0, 0xFFFF0000L), BigInteger.valueOf(0xFFFF0000L));
    bigInt(new UI128(0, 0xFFFF000000L), BigInteger.valueOf(0xFFFF000000L));
    bigInt(new UI128(0, 0xFFFFFF000000L), BigInteger.valueOf(0xFFFFFF000000L));
    bigInt(new UI128(0, 0xFFFF000000DEADL), BigInteger.valueOf(0xFFFF000000DEADL));
    bigInt(new UI128(0, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(64).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(72).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(80).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(88).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(96).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(104).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(112).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(120).subtract(BigInteger.ONE));
    bigInt(new UI128(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), BigInteger.valueOf(2).pow(128).subtract(BigInteger.ONE));
  }

  private void bigInt(UI128 original, BigInteger expected) {
    BigInteger actual = V6BigIntegers.toBigInteger(original.high, original.low);
    UI128 rev = V6BigIntegers.fromBigInteger(UI128::new, actual);
    assertEquals(expected, actual);
    assertEquals(original.high, rev.high);
    assertEquals(original.low, rev.low);
  }

  @Test
  public void testUint128Bytes() {
    bytes(new UI128(0, 0), new byte[16]);
    bytes(new UI128(0xfeedcafedeadbeefL, 0xbabebeefbabab1b1L), (byte) 0xFE, (byte) 0xED, (byte) 0xCA, (byte) 0xFE, (byte) 0xDE, (byte) 0xAD, (byte) 0xBE,(byte)  0xEF,(byte)  0xBA, (byte) 0xBE, (byte) 0xBE, (byte) 0xEF, (byte) 0xBA, (byte) 0xBA, (byte) 0xB1, (byte) 0xB1);
  }

  private void bytes(UI128 i, byte...expected) {
    byte[] actual = V6Bytes.toBytes(i.high, i.low);
    UI128 rev = V6Bytes.fromBytes(UI128::new, actual);
    assertArrayEquals(expected, actual);
    assertEquals(i, rev);
  }

  @Test
  public void testUint128MaskIndex() {
    assertEquals(-1, V6Masking.maskSizeIfBlock(0, 0b1, 0, 0b11));
    assertEquals(128, V6Masking.maskSizeIfBlock(0, 0, 0, 0));
    assertEquals(128, V6Masking.maskSizeIfBlock(0xFFFFFFFFFFFFFFFFL, 0x0, 0xFFFFFFFFFFFFFFFFL, 0x0));
    assertEquals(128, V6Masking.maskSizeIfBlock(0x0, 0xFFFFFFFFFFFFFFFFL, 0x0, 0xFFFFFFFFFFFFFFFFL));
    assertEquals(64, V6Masking.maskSizeIfBlock(0xCAFEBB0000000000L, 0, 0xCAFEBB0000000000L, 0xFFFFFFFFFFFFFFFFL));
    assertEquals(56, V6Masking.maskSizeIfBlock(0xCAFEBB0000000000L, 0, 0xCAFEBB00000000FFL, 0xFFFFFFFFFFFFFFFFL));
    assertEquals(-1, V6Masking.maskSizeIfBlock(0xDEADBB0000000000L, 0, 0xCAFEBB00000000FFL, 0xFFFFFFFFFFFFFFFFL));
    assertEquals(-1, V6Masking.maskSizeIfBlock(0, 0b01, 0, 0b10));
    assertEquals(-1, V6Masking.maskSizeIfBlock(0, 0b011, 0, 0b101));
    assertEquals(0, V6Masking.maskSizeIfBlock(0x0, 0x0, 0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL));
  }

  @Test
  public void testUint128Arithmetic() {
    BigInteger size = V6BigIntegers.toBigInteger(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL).add(BigInteger.ONE);
    UI128[] tests = {
      new UI128(0, 0),
      new UI128(0, 1),
      new UI128(0, 0xFFFFFFFFFFFFFFFFL),
      new UI128(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL),
      new UI128(0xFFFFFFFFFFFFFFFFL, 0),
      new UI128(10, 10),
      new UI128(1, 0),
      new UI128(0x123456789abcdef0L, 0x123456789abcdef0L),
    };
    for (UI128 i : tests) {
      for (UI128 j : tests) {
        BigInteger a = V6BigIntegers.toBigInteger(i.high, i.low);
        BigInteger b = V6BigIntegers.toBigInteger(j.high, j.low);
        {
          BigInteger expected = a.add(b).mod(size);
          UI128 result = V6Arithmetic.add(UI128::new, i.high, i.low, j.high, j.low);
          BigInteger actual = V6BigIntegers.toBigInteger(result.high, result.low);

          assertEquals(expected, actual, a + "+" + b);
        }
        {
          BigInteger expected = a.subtract(b).mod(size);
          UI128 result = V6Arithmetic.subtract(UI128::new, i.high, i.low, j.high, j.low);
          BigInteger actual = V6BigIntegers.toBigInteger(result.high, result.low);

          assertEquals(expected, actual, a + "-" + b);
        }
      }
    }
  }

  private static final class UI128 {
    final long high;
    final long low;

    UI128(long high, long low) {
      this.high = high;
      this.low = low;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UI128 ui128 = (UI128) o;
      return high == ui128.high && low == ui128.low;
    }

    @Override
    public int hashCode() {
      return Objects.hash(high, low);
    }
  }
}
