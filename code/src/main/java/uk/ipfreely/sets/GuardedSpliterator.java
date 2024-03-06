// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
//package uk.ipfreely.sets;
//
//import uk.ipfreely.Address;
//
//import java.util.Spliterator;
//import java.util.function.Consumer;
//
//import static uk.ipfreely.sets.Compare.less;
//import static uk.ipfreely.sets.Compare.lessOrEqual;
//import static uk.ipfreely.sets.Validation.validate;
//
//final class GuardedSpliterator<A extends Address<A>> implements Spliterator<A> {
//    private boolean done;
//    private A current;
//    private final A limit;
//
//    GuardedSpliterator(A first, A last, A guard) {
//        A limit = first.add(guard);
//        validate(lessOrEqual(first, last), "", first, IllegalArgumentException::new);
//        validate(less(limit, last), "", first, IllegalArgumentException::new);
//
//        current = first;
//        this.limit = limit;
//    }
//
//    @Override
//    public boolean tryAdvance(Consumer<? super A> action) {
//        if (done) {
//            throw new ExcessiveIterationException();
//        }
//        A ret = current;
//        done = ret.equals(limit);
//        current = current.next();
//        action.accept(ret);
//        return true;
//    }
//
//    @Override
//    public Spliterator<A> trySplit() {
//        // forbidden
//        return null;
//    }
//
//    @Override
//    public long estimateSize() {
//        A diff = limit.subtract(current).next();
//        long high = diff.highBits();
//        long low = diff.lowBits();
//        return high == 0 && low >= 0
//                ? low
//                : Long.MAX_VALUE;
//    }
//
//    @Override
//    public int characteristics() {
//        // TODO: revisit
//        return 0;
//    }
//}
