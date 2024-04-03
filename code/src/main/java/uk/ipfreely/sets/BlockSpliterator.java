// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.util.Spliterator;
import java.util.function.Consumer;

import static uk.ipfreely.sets.Validation.validate;

final class BlockSpliterator<A extends Address<A>> implements Spliterator<Block<A>> {

    // TODO: can replace with constant
    private static final double LOG_2 = Math.log(2.0);

    private A start;
    private final A end;

    BlockSpliterator(A start, A end) {
        Family<A> family = start.family();
        boolean internet = start.equals(family.min()) && end.equals(family.max());
        validate(!internet, "Cannot handle entire internet", family, IllegalArgumentException::new);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Block<A>> action) {
        if (start == null) {
            return false;
        }
        // https://blog.ip2location.com/knowledge-base/how-to-convert-ip-address-range-into-cidr/
        int maxSize = maxMask(start);
        A size = end.subtract(start).next();
        double x = log(size) / LOG_2;
        final int width = start.family().width();
        int maxDiff = (int) (width - Math.floor(x));
        if (maxSize < maxDiff) {
            maxSize = maxDiff;
        }
        Block<A> block = AddressSets.block(start, maxSize);
        A last = block.last();
        start = last.equals(end) ? null : last.next();
        action.accept(block);
        return true;
    }

    @Override
    public Spliterator<Block<A>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL;
    }

    private double log(A ip) {
        // https://stackoverflow.com/a/7982137
        int MAX_DIGITS_2 = 977;

        int blex = bitLen(ip) - MAX_DIGITS_2; // any value in 60..1023 works here
        if (blex > 0)
            ip = ip.shift(blex);
        double res = Math.log(toDouble(ip));
        return blex > 0 ? res + blex * LOG_2 : res;
    }

    private int bitLen(A ip) {
        long high = ip.highBits();
        return high == 0L
                ? Long.bitCount(ip.lowBits())
                : Long.bitCount(high) + 128;
    }

    private double toDouble(A ip) {
        // TODO: something better
        return ip.toBigInteger().doubleValue();
    }

    private int maxMask(A ip) {
        final int width = ip.family().width();
        int n = 0;
        long low = ip.lowBits();
        for(int bits = Math.min(width, 64); n < bits; n++) {
            if ((low & 1) == 1) {
                return width - n;
            }
            low >>>= 1;
        }
        if (width < 64) {
            return 0;
        }
        long high = ip.highBits();
        for(; n < 128; n++) {
            if ((high & 1) == 1) {
                return width - n;
            }
            low >>>= 1;
        }
        return 0;

//        List<A> masks = ip.family().masks();
//        for (int i = 0, s = masks.size(); i < s; i++) {
//            A mask = masks.get(i);
//            if (ip.and(mask).equals(ip)) {
//                return i;
//            }
//        }
//        throw new AssertionError();
    }
}

