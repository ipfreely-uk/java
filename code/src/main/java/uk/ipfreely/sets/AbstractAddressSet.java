package uk.ipfreely.sets;

import uk.ipfreely.Address;

import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

abstract class AbstractAddressSet<A extends Address<A>> implements AddressSet<A> {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AddressSet)) {
            return false;
        }
        AddressSet<?> other = (AddressSet<?>) obj;

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

    @Override
    public String toString() {
        final int BUF_SIZE = (8 * 4 + 7) * (4 * 2 + 2) + 4;

        List<Range<?>> ranges = ranges().limit(6).collect(toList());
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append('{');
        String delim = "";
        for (int i = 0; i < Math.min(5, ranges.size()); i++) {
            buf.append(delim);
            delim = "; ";
            buf.append(ranges.get(i));
        }
        if (ranges.size() == 6) {
            buf.append(";...");
        }
        return buf.append('}').toString();
    }
}
