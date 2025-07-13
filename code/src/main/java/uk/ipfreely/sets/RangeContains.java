package uk.ipfreely.sets;

import uk.ipfreely.Addr;

final class RangeContains {
    private RangeContains() {}

    static  <A extends Addr<A>> boolean next(Range<A> r, A address) {
        return !address.family().max().equals(address)
                && r.contains(address.next());
    }

    static <A extends Addr<A>> boolean prev(Range<A> r, A address) {
        return !address.family().min().equals(address)
                && r.contains(address.prev());
    }
}
