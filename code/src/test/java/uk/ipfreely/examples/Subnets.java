package uk.ipfreely.examples;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.sets.*;

import java.math.BigInteger;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public final class Subnets {

    private Subnets() {}

    /**
     * Creates a random CIDR address block of desired size from given candidates.
     *
     * @param ranges candidate super-blocks
     * @param desiredSize number of IP addresses required
     * @param rng random number generator
     * @return block, if possible
     * @param <A> address type
     */
    public static <A extends Address<A>> Optional<Block<A>> random(AddressSet<A> ranges, BigInteger desiredSize, IntSupplier rng) {
        BigInteger s = ranges.size();


        List<Block<A>> candidates = ranges.ranges()
                .flatMap(Range::blocks)
                .filter(b -> b.size().compareTo(desiredSize) >= 0)
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        A pick;
        if (candidates.size() == 1) {
            pick = RandomAddress.from(candidates.get(0), rng);
        } else {
            A size = total(candidates);
            A zero = size.family().min();
            A ran = RandomAddress.from(AddressSets.range(zero, size), rng);
            Block<A> containerBlock = blockAt(candidates, ran);
            pick = RandomAddress.from(containerBlock, rng);
        }
        Family<A> family = pick.family();
        int bits = maskBitsFor(family, desiredSize);
        A mask = family.masks().get(bits);
        A first = mask.or(pick);
        return Optional.of(AddressSets.block(first, bits));
    }

    private static <A extends Address<A>> Block<A> blockAt(List<Block<A>> blocks, A ran) {
        A current = ran.family().min();
        for (Block<A> block : blocks) {
            current = size(block).add(current);
            if (current.compareTo(ran) >= 0) {
                return block;
            }
        }
        throw new AssertionError();
    }

    private static <A extends Address<A>> A total(List<Block<A>> blocks) {
        A size = size(blocks.get(0));
        for (int i = 1, len = blocks.size(); i < len; i++) {
            size = size(blocks.get(i)).add(size);
        }
        return size;
    }

    private static <A extends Address<A>> A size(Block<A> block) {
        return block.last().subtract(block.first()).next();
    }

    /**
     * See RFC 1918.
     *
     * @return set of private IPv4 ranges
     */
    public static AddressSet<V4> privateV4() {
        Range<V4> classA = AddressSets.parseCidr(Family.v4(), "10.0.0.0/8");
        Range<V4> classB = AddressSets.parseCidr(Family.v4(), "176.16.0.0/12");
        Range<V4> classC = AddressSets.parseCidr(Family.v4(), "192.168.0.0/16");
        return AddressSets.of(classA, classB, classC);
    }

    /**
     * Calculates the necessary mask to accommodate the desired subnet size.
     *
     * @param family IP family
     * @param size minimum desired size
     * @return mask bits
     */
    public static <A extends Address<A>> int maskBitsFor(Family<A> family, A size) {
        List<A> masks = family.masks();
        for (int i = masks.size() - 1; i >= 0; i--) {
            if (size.compareTo(masks.get(i).not()) <= 0) {
                return i;
            }
        }
        // unreachable
        throw new AssertionError();
    }

    /**
     * Calculates the necessary mask to accommodate the desired subnet size.
     *
     * @param family IP family
     * @param size minimum desired size
     * @return mask bits
     */
    public static <A extends Address<A>> int maskBitsFor(Family<A> family, BigInteger size) {
        if (size.equals(family.max().toBigInteger())) {
            return 0;
        }
        return maskBitsFor(family, family.parse(size));
    }


}
