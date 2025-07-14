// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.Spliterator;
import java.util.function.Consumer;

import static uk.ipfreely.sets.Validation.validate;

final class BlockSpliterator<A extends Addr<A>> implements Spliterator<Block<A>> {

    // Math.log(2.0)
    private static final double LOG_2 = 0.6931471805599453;

    private A start;
    private final A end;

    BlockSpliterator(A start, A end) {
        var family = start.family();
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
        final int width = start.family().width();
        int maxSize = width - start.trailingZeros();
        A size = end.subtract(start).next();
        double l = Math.log(size.doubleValue());
        double x = l / LOG_2;
        int maxDiff = (int) (width - Math.floor(x));
        int maskSize = Math.max(maxSize, maxDiff);
        var block = AddressSets.block(start, maskSize);
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
        return IMMUTABLE | DISTINCT | NONNULL;
    }
}
