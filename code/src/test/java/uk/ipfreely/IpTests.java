// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public abstract class IpTests<A extends Address<A>> {

  protected <T> void testToAndFrom(Family<A> internet,
                               Function<A, T> to,
                               Function<T, A> from,
                               Predicate<T> validator) {
    for (A ip : internet.masks()) {
      T value = to.apply(ip);
      A actual = from.apply(value);

      assertTrue(validator.test(value), ip.toString());
      assertEquals(ip, actual, ip.toString());
    }
  }

  public static void expect(String s, Class<? extends Exception> c, Callable<?> lambda) {
    try {
      Object o = lambda.call();
      fail(s + ". Expected " + c + "; got " + o);
    } catch (Exception e) {
      assertTrue(c.isInstance(e), "Wanted " + c + "; got " + e);
    }
  }

  protected void testFeatures(A min, A max) {
    // network size
    Family<?> ver = min.family();
    BigInteger expectedSize = BigInteger.valueOf(2).pow(ver.width());
    assertEquals(expectedSize, min.family().maskAddressCount(0), ver.toString());
    assertThrowsExactly(IllegalArgumentException.class, () -> min.family().maskAddressCount(min.family().width() + 1));
    assertThrowsExactly(IllegalArgumentException.class, () -> min.family().maskAddressCount(-1));
    // equality
    A expected = max.prev().prev();
    A actual = max.prev().prev();
    assertNotSame(expected, actual);
    assertNotEquals(expected, max);
    assertEquals(expected, actual);
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(max, expected.next().next());
    assertEquals(expected.toString(), actual.toString());
    // NOT
    assertEquals(min, max.not());
    assertEquals(max, min.not());
    assertEquals(min, min.not().not());
    // AND
    assertEquals(min, max.and(min));
    assertEquals(max, max.and(max));
    assertEquals(min, min.and(min));
    // OR
    assertEquals(max, max.or(min));
    assertEquals(max, max.or(max));
    assertEquals(min, min.or(min));
    // XOR
    assertEquals(max, max.xor(min));
    assertEquals(min, max.xor(max));
    assertEquals(min, min.xor(min));
    // compareTo
    assertTrue(min.compareTo(max) < 0);
    assertTrue(max.compareTo(min) > 0);
    assertEquals(0, max.compareTo(max));
    // overflow
    assertEquals(min, max.next());
    assertEquals(max, min.prev());
  }

  @SuppressWarnings("unchecked")
  protected void testArithmetic(Family<A> internet, A...ips) {
    A last = internet.masks().get(internet.masks().size() - 1);
    BigInteger max = last.toBigInteger();
    BigInteger size = max.add(BigInteger.ONE);

    for (A a : ips) {
      BigInteger ba = a.toBigInteger();

      {
        double expected = ba.doubleValue();
        double actual = a.doubleValue();
        assertEquals(expected, actual, a.toString());
      }
      {
        // NOT
        A actual = a.not();
        assertNotEquals(a, actual);
        assertEquals(a, actual.not());
      }

      for (A b : ips) {
        BigInteger bb = b.toBigInteger();
        {
          // add
          BigInteger ex = ba.add(bb);
          BigInteger mod = ex.mod(size);
          A expected = internet.parse(mod);
          A actual = a.add(b);
          assertEquals(expected, actual, a + "+" + b);
        }
        {
          // subtract
          BigInteger ex = ba.subtract(bb);
          BigInteger mod = ex.mod(size);
          A expected = internet.parse(mod);
          A actual = a.subtract(b);
          assertEquals(expected, actual, a + "-" + b);
        }
        {
          // multiply
          BigInteger ex = ba.multiply(bb);
          BigInteger mod = ex.mod(size);
          A expected = internet.parse(mod);
          A actual = a.multiply(b);
          assertEquals(expected, actual, a + "*" + b);
        }
        {
          // divide
          if (BigInteger.ZERO.equals(bb)) {
            // divide by zero
            expect(a + "/" + b, ArithmeticException.class, () -> a.divide(b));
          } else {
            BigInteger ex = ba.divide(bb);
            BigInteger mod = ex.mod(size);
            A expected = internet.parse(mod);
            A actual = a.divide(b);
            assertEquals(expected, actual, a + "/" + b);
          }
        }
        {
          // modulus
          if (BigInteger.ZERO.equals(bb)) {
            // divide by zero
            expect(a + "%" + b, ArithmeticException.class, () -> a.mod(b));
          } else {
            BigInteger ex = ba.mod(bb);
            BigInteger mod = ex.mod(size);
            A expected = internet.parse(mod);
            A actual = a.mod(b);
            assertEquals(expected, actual, a + "%" + b);
          }
        }
        {
          // AND
          A expected = internet.parse(ba.and(bb));
          A actual = a.and(b);
          assertEquals(expected, actual, a + "&" + b);
        }
        {
          // OR
          A expected = internet.parse(ba.or(bb));
          A actual = a.or(b);
          assertEquals(expected, actual, a + "|" + b);
        }
        {
          // XOR
          A expected = internet.parse(ba.xor(bb));
          A actual = a.xor(b);
          assertEquals(expected, actual, a + "^" + b);
        }
      }
    }
  }

  protected void testCharSequence(Function<String, ? extends CharSequence> parser, String... ips) {
    for (String ip : ips) {
      CharSequence sq = parser.apply(ip);
      assertEquals(ip.length(), sq.length(), ip);
      for (int i = 0; i < ip.length(); i++) {
        char expected = ip.charAt(i);
        char actual = sq.charAt(i);
        assertEquals(String.valueOf(expected), String.valueOf(actual), ip);
      }
    }
  }
}
