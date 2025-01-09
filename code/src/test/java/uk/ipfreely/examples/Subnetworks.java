package uk.ipfreely.examples;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.Subnets;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Block;

import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Subnetworks {

    /**
     * @param initial available range
     * @param count min number of allocatable addresses required
     * @return stream of subnets
     * @param <A> address family
     */
    static <A extends Addr<A>> Stream<Block<A>> divide(Block<A> initial, BigInteger count) {
        Family<A> f = initial.first().family();
        Subnets<A> s = f.subnets();
        BigInteger min = f == Family.v4()
                ? count.add(BigInteger.valueOf(2))
                : count;
        int bits = f.width();
        int maskBits = IntStream.rangeClosed(0, f.width())
                .map(i -> bits - i)
                .filter(m -> s.count(m).compareTo(min) >= 0)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        return initial.subnets(maskBits);
    }

    public static void main(String[] args) {
        V6 netAddr = Family.v6().parse("2001:db8:dead:cafe::");
        Block<V6> initial = AddressSets.block(netAddr, 119);
        BigInteger oneHundredAddresses = BigInteger.valueOf(100);

        divide(initial, oneHundredAddresses)
                .forEach(System.out::println);
    }
}
