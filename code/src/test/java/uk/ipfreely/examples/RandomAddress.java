package uk.ipfreely.examples;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.collections.Block;
import uk.ipfreely.collections.Range;

import java.util.function.IntSupplier;

/**
 * Utility type for randomly generating addresses.
 */
public final class RandomAddress {

    private RandomAddress() {}

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
            return family.fromUint(rng.getAsInt());
        }
        return family.parse(randomLong(rng), randomLong(rng));
    }

    private static long randomLong(IntSupplier rng) {
        final long intMask = 0xFFFFFFFFL;
        long l = (rng.getAsInt() &  intMask) << Integer.SIZE;
        return l | (rng.getAsInt() &  intMask);
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
     * Random address from the given IP CIDR block.
     *
     * @param block CIDR block
     * @param rng random number generator
     * @return random address
     * @param <A> address type
     */
    public static <A extends Address<A>> A fromBlock(Block<A> block, IntSupplier rng) {
        A first = block.first();
        A last = block.last();
        // if it's a single address, nothing to do
        if (first.equals(last)) {
            return first;
        }
        Family<A> family = first.family();
        A random = generate(family, rng);
        return fromBlock(family, block, random);
    }

    private static <A extends Address<A>> A fromBlock(Family<A> family, Block<A> block, A random) {
        A inverseMask = family.masks()
                .get(block.maskBits())
                .not();
        return inverseMask.and(random)
                .or(block.first());
    }
}
