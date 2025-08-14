package uk.ipfreely.sets;

import uk.ipfreely.Addr;

final class Adjacency {

    private Adjacency() {}

    static <A extends Addr<A>, R extends Range<A>> boolean test(R r0, R r1) {
        return lastIsAdjacentToFirst(r0, r1)
                || lastIsAdjacentToFirst(r1, r0);
    }

    private static  <A extends Addr<A>, R extends Range<A>> boolean lastIsAdjacentToFirst(R r0, R r1) {
        A zero = r0.family().min();
        A first = r1.first();
        if (first.equals(zero)) {
            return false;
        }
        A prev = first.prev();
        A last = r0.last();
        return prev.equals(last);
    }
}
