// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.Iterator;

abstract class AbstractAddressSet<A extends Addr<A>> implements AddressSet<A> {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof AddressSet<?> other) && eq(other);
    }

    private boolean eq(AddressSet<?> other) {
        if (!other.size().equals(size())) {
            return false;
        }

        Iterator<?> me = ranges().iterator();
        Iterator<?> you = other.ranges().iterator();
        while(me.hasNext() && you.hasNext()) {
            Object mine = me.next();
            Object yours = you.next();
            if (!mine.equals(yours)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return ranges().mapToInt(Object::hashCode).reduce(0, (n, r) -> n * 31 + r);
    }
}
