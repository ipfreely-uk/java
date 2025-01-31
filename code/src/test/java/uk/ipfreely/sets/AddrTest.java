// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.V4;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ipfreely.Family.v4;

public class AddrTest {

  @Test
  void testAddress() {
    Block<V4> addr = AddressSets.address(v4().parse("127.0.0.1"));
    assertEquals(BigInteger.ONE, addr.size());
    assertEquals(v4().width(), addr.maskSize());
    assertEquals(v4().parse("255.255.255.255"), addr.mask());
  }
}
