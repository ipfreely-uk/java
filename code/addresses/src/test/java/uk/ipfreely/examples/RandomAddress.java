// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.examples;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Block;
import uk.ipfreely.sets.Range;

import java.util.Random;
import java.util.function.IntSupplier;

/**
 * Utility type for randomly generating addresses.
 */
public final class RandomAddress {

    private static final IntSupplier RNG = new Random()::nextInt;

    private RandomAddress() {}

    public static void main(String[] args) {
        Block<V6> subnet = AddressSets.parseCidr(Family.v6(), "2001:db8:cafe:babe::/64");
        V6 address = from(subnet, RNG);
        System.out.println(address);
    }

    /**
     * Random address from the given IP range.
     *
     * @param range address range
     * @param rng   random number generator
     * @param <A>   address type
     * @return random address
     */
    public static <A extends Address<A>> A from(Range<A> range, IntSupplier rng) {
        A first = range.first();
        A last = range.last();
        // if it's a single address, nothing to do
        if (first.equals(last)) {
            return first;
        }
        Family<A> family = first.family();
        A random = generate(family, rng);
        // if it's a block, just mask out unwanted bits
        if (range instanceof Block) {
            return fromBlock(family, (Block<A>) range, random);
        }
        // else do it the long way
        A diff = last.subtract(first);
        A betweenZeroAndDiff;
        if (random.compareTo(diff) > 0) {
            A one = family.min().next();
            A size = diff.add(one);
            betweenZeroAndDiff = random.mod(size);
        } else {
            betweenZeroAndDiff = random;
        }
        return betweenZeroAndDiff.add(first);
    }

    /**
     * Returns random IP address.
     *
     * @param family the IP family
     * @param rng    random number generator
     * @param <A>    the IP type
     * @return random address
     */
    public static <A extends Address<A>> A generate(Family<A> family, IntSupplier rng) {
        if (family == Family.v4()) {
            return family.parse(rng.getAsInt());
        }
        return family.parse(randomLong(rng), randomLong(rng));
    }

    private static long randomLong(IntSupplier rng) {
        final long intMask = 0xFFFFFFFFL;
        long l = (rng.getAsInt() &  intMask) << Integer.SIZE;
        return l | (rng.getAsInt() &  intMask);
    }

    private static <A extends Address<A>> A fromBlock(Family<A> family, Block<A> block, A random) {
        A inverseMask = family.subnets().masks()
                .get(block.maskSize())
                .not();
        return inverseMask.and(random)
                .or(block.first());
    }
}
