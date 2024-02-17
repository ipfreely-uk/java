package uk.ipfreely.collections;

import uk.ipfreely.Address;

import java.util.Iterator;

import static uk.ipfreely.collections.Compare.less;
import static uk.ipfreely.collections.Compare.lessOrEqual;
import static uk.ipfreely.collections.Validation.validate;

final class GuardedIterator<A extends Address<A>> implements Iterator<A> {

    private boolean done;
    private A current;
    private final A limit;

    GuardedIterator(A first, A last, A guard) {
        A limit = first.add(guard);
        validate(lessOrEqual(first, last), "", first, IllegalArgumentException::new);
        validate(less(limit, last), "", first, IllegalArgumentException::new);

        current = first;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public A next() {
        if (done) {
            throw new ExcessiveIterationException();
        }
        A ret = current;
        done = ret.equals(limit);
        current = current.next();
        return ret;
    }
}
