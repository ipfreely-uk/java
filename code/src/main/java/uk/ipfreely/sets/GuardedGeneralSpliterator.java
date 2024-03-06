// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
//package uk.ipfreely.sets;
//
//import uk.ipfreely.Address;
//
//import java.util.Spliterator;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//final class GuardedGeneralSpliterator<E, A extends Address<A>> implements Spliterator<E>, Consumer<E> {
//
//    private final Spliterator<E> delegated;
//    private final A limit;
//    private A current;
//    private final Function<E, E> fn;
//    private E accepted;
//
//    GuardedGeneralSpliterator(Spliterator<E> delegated, A limit, Function<E, E> fn) {
//        this.delegated = delegated;
//        this.limit = limit;
//        current = limit.family().min();
//        this.fn = fn;
//    }
//
//    @Override
//    public boolean tryAdvance(Consumer<? super E> action) {
//        if (Compare.greater(limit, current)) {
//            throw new ExcessiveIterationException();
//        }
//        boolean found = delegated.tryAdvance(this);
//        if (found) {
//            action.accept(fn.apply(accepted));
//            current = current.next();
//        }
//        return found;
//    }
//
//    @Override
//    public Spliterator<E> trySplit() {
//        // forbidden
//        return null;
//    }
//
//    @Override
//    public long estimateSize() {
//        return delegated.estimateSize();
//    }
//
//    @Override
//    public int characteristics() {
//        // TODO: revisit
//        return 0;
//    }
//
//    @Override
//    public void accept(E e) {
//        this.accepted = e;
//    }
//}
