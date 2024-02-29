package uk.ipfreely;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import static uk.ipfreely.Validation.validate;

/**
 * A list of all possible Ip6 masks including 0
 */
class V6MaskList extends AbstractList<V6> implements RandomAccess {

    private static final int SIZE = Long.SIZE * 2 + 1;
    private static final int FIRST_LOW_IDX = SIZE / 2;

    private final V6Function<V6> source;

    V6MaskList(V6Function<V6> source) {
        this.source = source;
    }

    static final List<V6> MASKS = new V6MaskList(V6::fromLongs);

    @Override
    public V6 get(int index) {
        validate(index >= 0 && index < SIZE, "Index must be between 0 and " + SIZE, index, IndexOutOfBoundsException::new);
        if (index < FIRST_LOW_IDX) {
            return source.apply(nth(index), 0L);
        }
        return source.apply(0xffffffffffffffffL, nth(index - FIRST_LOW_IDX));
    }

    private long nth(int index) {
        long ip = 0;
        for (int i = 0; i < index; i++) {
            ip >>>= 1;
            ip |= 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        }
        return ip;
    }

    @Override
    public int size() {
        return SIZE;
    }
}
