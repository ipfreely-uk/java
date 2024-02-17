package uk.ipfreely.collections;

import uk.ipfreely.Address;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility type for creating {@link AddressSet} instances.
 * Unlike {@link Range}, {@link AddressSet} can be empty.
 */
public final class AddressSets {
    private AddressSets() {}

    /**
     * Factory method for immutable {@link AddressSet}.
     *
     * @param ranges constituent ranges
     * @return set of addresses
     * @param <A> address type
     */
    @SafeVarargs
    public static <A extends Address<A>> AddressSet<A> of(Range<A>... ranges) {
        return from(Arrays.asList(ranges));
    }

    /**
     * Factory method for immutable {@link AddressSet}.
     *
     * @param ranges constituent ranges
     * @return set of addresses
     * @param <A> address type
     */
    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> AddressSet<A> from(Collection<Range<A>> ranges) {
        if (ranges.isEmpty()) {
            return (AddressSet<A>) Empty.IMPL;
        }
        final Range<A>[] data = rationalize(ranges);
        if (data.length == 1) {
            return new SingletonSet<>(data[0]);
        }
        Arrays.sort(data, AddressSets::compare);
        return new ArraySet<>(data);
    }

    /**
     * <p>
     *     Guards a set against excessive iteration.
     *     See {@link ExcessiveIterationException} for details.
     * </p>
     * <p>
     *     If {@link AddressSet#size()} is less than the guard's {@link Address#toBigInteger()} value
     *     the unguarded set is returned.
     * </p>
     *
     * @param set set to guard
     * @param guard guard value
     * @return guarded set
     * @param <A> address type
     */
    public static <A extends Address<A>> AddressSet<A> guarded(AddressSet<A> set, A guard) {
        if (set.getClass() == GuardedSet.class) {
            GuardedSet<A> gs = (GuardedSet<A>) set;
            if (gs.guard.equals(guard)) {
                return set;
            }
            return new GuardedSet<>(gs.delegate, guard);
        }
        if (Compare.less(set.size(), guard.toBigInteger())) {
            return set;
        }
        return new GuardedSet<>(set, guard);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Address<A>> Range<A>[] rationalize(Collection<Range<A>> ranges) {
        SortedSet<Range<A>> set = new TreeSet<>(AddressSets::compare);
        rationalize(set, ranges);
        return set.toArray(new Range[0]);
    }

    private static <A extends Address<A>> void rationalize(SortedSet<Range<A>> set, Collection<Range<A>> ranges) {
        for (Range<A> range : ranges) {
            Iterator<Range<A>> it = set.iterator();
            while(it.hasNext()) {
                Range<A> candidate = it.next();
                if (cannotCombine(candidate, range)) {
                    break;
                }
                Optional<Range<A>> combined = Ranges.combine(range, candidate);
                if (combined.isPresent()) {
                    it.remove();
                    range = combined.get();
                }
            }
            set.add(range);
        }
    }

    private static <A extends Address<A>> boolean cannotCombine(Range<A> candidate, Range<A> range) {
        return Compare.less(range.last(), candidate.first())
                && !range.last().next().equals(candidate.first());
    }

    private static <A extends Address<A>> int compare(Range<A> r0, Range<A> r1) {
        return r0.first().compareTo(r1.first());
    }

    private static final class Empty<A extends Address<A>> extends AbstractAddressSet<A> {
        static final AddressSet<?> IMPL = new Empty<>();

        @Override
        public boolean contains(Address<?> address) {
            return false;
        }

        @Override
        public Iterator<A> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Stream<Range<A>> ranges() {
            return Stream.empty();
        }

        @Override
        public BigInteger size() {
            return BigInteger.ZERO;
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

    private static final class ArraySet<A extends Address<A>> extends AbstractAddressSet<A> {
        private final Range<A>[] ranges;

        ArraySet(Range<A>[] ranges) {
            this.ranges = ranges;
        }

        @Override
        public Stream<Range<A>> ranges() {
            return Stream.of(ranges);
        }

        @Override
        public boolean contains(Address<?> address) {
            for (Range<A> range : ranges) {
                if (range.contains(address)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Iterator<A> iterator() {
            return new RangeArrayIterator<>(ranges);
        }
    }

    private static final class SingletonSet<A extends Address<A>> extends AbstractAddressSet<A> {
        private final Range<A> range;

        SingletonSet(Range<A> range) {
            this.range = Objects.requireNonNull(range);
        }

        @Override
        public boolean contains(Address<?> address) {
            return range.contains(address);
        }

        @Override
        public Stream<Range<A>> ranges() {
            return Stream.of(range);
        }

        @Override
        public BigInteger size() {
            return range.size();
        }

        @Override
        public Iterator<A> iterator() {
            return range.iterator();
        }
    }

    private static final class GuardedSet<A extends Address<A>> extends AbstractAddressSet<A> {
        private final AddressSet<A> delegate;
        private final A guard;

        private GuardedSet(AddressSet<A> delegate, A guard) {
            this.delegate = delegate;
            this.guard = guard;
        }

        @Override
        public boolean contains(Address<?> address) {
            return delegate.contains(address);
        }

        @Override
        public Iterator<A> iterator() {
            return new GuardedDecoratingIterator<>(delegate.iterator(), guard);
        }

        @Override
        public Spliterator<A> spliterator() {
            Spliterator<A> s = delegate.spliterator();
            return new GuardedGeneralSpliterator<>(s, guard, a -> a);
        }

        @Override
        public Stream<Range<A>> ranges() {
            Spliterator<Range<A>> s = delegate.ranges().spliterator();
            Spliterator<Range<A>> guarded = new GuardedGeneralSpliterator<>(s, guard, this::range);
            return StreamSupport.stream(guarded, false);
        }

        private Range<A> range(Range<A> r) {
            return Ranges.guarded(r, guard);
        }
    }
}
