// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
//package uk.ipfreely.sets;
//
//import uk.ipfreely.Address;
//
//import java.util.Iterator;
//
//final class GuardedDecoratingIterator<A extends Address<A>> implements Iterator<A> {
//
//    private final Iterator<A> delegate;
//    private final A guard;
//    private A count;
//
//    public GuardedDecoratingIterator(Iterator<A> delegate, A guard) {
//        this.delegate = delegate;
//        this.guard = guard;
//        count = guard.family().min();
//    }
//
//    @Override
//    public boolean hasNext() {
//        return true;
//    }
//
//    @Override
//    public A next() {
//        if (Compare.less(guard, count)) {
//            throw new ExcessiveIterationException();
//        }
//        count = count.next();
//        return delegate.next();
//    }
//}
