// Copyright 2024-2025 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;
import uk.ipfreely.ParseException;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static uk.ipfreely.sets.Validation.validate;

/**
 * <p>Static factory methods for creating {@link AddressSet}s, {@link Range}s and {@link Block}s.</p>
 */
public final class AddressSets {
    private AddressSets() {}

    /**
     * <p>Factory method for {@link AddressSet}.</p>
     * <p>
     *     If all members form a contiguous range, returns {@link Range}.
     *     If all members form CIDR block, returns {@link Block}.
     * </p>
     * <pre><code>
     *     // EXAMPLE
     *     Range&lt;V4&gt; classA = AddressSets.parseCidr(Family.v4(), "10.0.0.0/8");
     *     Range&lt;V4&gt; classB = AddressSets.parseCidr(Family.v4(), "176.16.0.0/12");
     *     Range&lt;V4&gt; classC = AddressSets.parseCidr(Family.v4(), "192.168.0.0/16");
     *     // <a href="https://datatracker.ietf.org/doc/html/rfc1918#section-3">RFC-1918 Address Allocation for Private Internets</a>
     *     AddressSet&lt;V4&gt; privateRanges = AddressSets.of(classA, classB, classC);
     * </code></pre>
     *
     * @param sets source sets
     * @return union of given sets
     * @param <A> address type
     * @param <S> range type
     */
    @SafeVarargs
    public static <A extends Addr<A>, S extends AddressSet<A>> AddressSet<A> of(S... sets) {
        return from(Arrays.asList(sets));
    }

    /**
     * <p>Version of {@link #of(AddressSet[])} intended for standard collections.</p>
     * <pre><code>
     *     // EXAMPLE
     *     AddressSet&lt;V4&gt; empty = AddressSets.from(Collections.emptySet());
     * </code></pre>
     * <pre><code>
     *     // EXAMPLE
     *     List&lt;String&gt; addresses = Arrays.asList("192.168.0.1", "192.168.0.10", "192.168.0.11", "192.168.0.12");
     *     // 4 entries: "192.168.0.1/32", "192.168.0.10/32", "192.168.0.11/32", "192.168.0.12/32"
     *     Set&lt;Block&lt;V4&gt;&gt; raw = addresses.stream()
     *                 .map(Family.v4()::parse)
     *                 .map(AddressSets::address)
     *                 .collect(Collectors.toSet());
     *     // 2 entries: "192.168.0.1/32", "192.168.0.10-192.168.0.12"
     *     Set&lt;Range&lt;V4&gt;&gt; rationalized = AddressSets.from(raw)
     *                 .ranges()
     *                 .collect(Collectors.toSet());
     * </code></pre>
     *
     * @param sets source sets
     * @return union of given sets
     * @param <A> address type
     * @param <S> set type
     */
    @SuppressWarnings("unchecked")
    public static <A extends Addr<A>, S extends AddressSet<A>> AddressSet<A> from(Iterable<S> sets) {
        SortedSet<Range<A>> results = new TreeSet<>(AddressSets::compare);
        for (S set : sets) {
            set.ranges().forEach(r -> rationalize(results, r));
        }
        final Range<A>[] data = results.toArray(new Range[0]);
        if (data.length == 0) {
            return (AddressSet<A>) Empty.IMPL;
        }
        if (data.length == 1) {
            return data[0];
        }
        return new ArraySet<>(data);
    }

    private static <A extends Addr<A>> void rationalize(SortedSet<Range<A>> target, Range<A> r) {
        Range<A> candidate = r;
        Iterator<Range<A>> it = target.iterator();
        while (it.hasNext()) {
            Range<A> next = it.next();
            if (next.contiguous(candidate)) {
                candidate = candidate.extremes(next);
                it.remove();
            } else if (compare(r, next) < 0) {
                break;
            }
        }
        target.add(candidate);
    }

    private static <A extends Addr<A>> int compare(Range<A> r0, Range<A> r1) {
        return r0.first().compareTo(r1.first());
    }

    /**
     * Single address as {@link Block}.
     *
     * @param address IP
     * @param <A> IP version
     * @return immutable instance
     */
    public static <A extends Addr<A>> Block<A> address(final A address) {
        final class Single extends AbstractRange<A> implements Block<A> {
            @Override
            public A first() {
                return address;
            }

            @Override
            public A last() {
                return address;
            }

            @Override
            public BigInteger size() {
                return BigInteger.ONE;
            }

            @Override
            public int maskSize() {
                return first().family().width();
            }

            @Override
            public String toString() {
                return "{" + cidrNotation() + "}";
            }
        }

        return new Single();
    }

    /**
     * <p>
     *     Creates {@link Block} from network address and mask size.
     *     The {@code maskSize} must be greater or equal to zero
     *     and less than or equal to {@link Family#width()}.
     *     The mask must cover all the true bits of the address.
     * </p>
     *
     * @param first    the first IP in the block
     * @param maskSize the number of mask bits
     * @param <A>      the Ip type
     * @return the block instance
     */
    public static <A extends Addr<A>> Block<A> block(final A first, final int maskSize) {
        final Family<A> family = first.family();
        int width = family.width();
        if (maskSize == width) {
            return address(first);
        }

        validate(maskSize >= 0, "Mask bits cannot be less than 0", maskSize, IllegalArgumentException::new);
        validate(maskSize <= width, "Mask must not exceed address width 32 (IPv4) or 128 (IPv6)", maskSize, IllegalArgumentException::new);
        validate(first.trailingZeros() >= width - maskSize, "Mask must cover network address bits", maskSize, IllegalArgumentException::new);

        final List<A> masks = family.subnets().masks();
        final A complement = masks.get(maskSize).not();
        final A last = first.or(complement);
        return block(first, last);
    }

    /**
     * <p>
     *     Creates a block from the given addresses which MUST form a valid CIDR block.
     * </p>
     * <p>
     *     Use {@link uk.ipfreely.Subnets#maskBits(Addr, Addr)} to test for valid blocks.
     * </p>
     *
     * @param first address
     * @param last address which must be greater or equal to the first address
     * @param <A> IP version
     * @return block
     */
    public static <A extends Addr<A>> Block<A> block(final A first, final A last) {
        if (first.equals(last)) {
            return address(first);
        }

        final class AddressBlock extends AbstractRange<A> implements Block<A> {

            @Override
            public A first() {
                return first;
            }

            @Override
            public A last() {
                return last;
            }

            @Override
            public String toString() {
                return "{" + cidrNotation() + "}";
            }
        }

        Block<A> block = new AddressBlock();
        validate(first.family().subnets().maskBits(first, last) >= 0, "Not an IP block", block, IllegalArgumentException::new);
        return block;
    }

    /**
     * Creates {@link Range} instance.
     * Returns {@link Block} when the range is a valid CIDR block.
     *
     * @param first first element
     * @param last  last element which MUST be greater or equal to first
     * @param <A>   the IP type
     * @return an immutable range of IP addresses
     */
    public static <A extends Addr<A>> Range<A> range(A first, A last) {
        final class AddressRange extends AbstractRange<A> {

            @Override
            public A first() {
                return first;
            }

            @Override
            public A last() {
                return last;
            }
        }

        int c = first.compareTo(last);
        if (c == 0) {
            return address(first);
        }
        validate(c < 0, "First address must be less than or equal to last", first, IllegalArgumentException::new);

        int maskSize = first.family().subnets().maskBits(first, last);
        return maskSize < 0
                ? new AddressRange()
                : block(first, last);
    }

    /**
     * Parses a CIDR string form as defined by <a href="https://tools.ietf.org/html/rfc4632">RFC4632</a>.
     * Example: {@code "127.0.0.1/32"}.
     *
     * @param cidrBlock the CIDR notation string
     * @return the block instance
     * @throws ParseException on invalid expression
     */
    public static Block<?> parseCidr(String cidrBlock) {
        final int stroke = cidrBlock.lastIndexOf('/');
        validate(stroke >= 0, "CIDR notation is 'ip/mask'", cidrBlock, ParseException::new);

        final String s1 = cidrBlock.substring(0, stroke);
        final String s2 = cidrBlock.substring(stroke + 1);

        @SuppressWarnings("rawtypes")
        Addr address = Family.unknown(s1);
        int mask = Integer.parseInt(s2);
        try {
            @SuppressWarnings("unchecked")
            Block<?> b = block(address, mask);
            return b;
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
    }

    /**
     * As {@link #parseCidr(String)} with version expectation.
     *
     * @param family    the IP family
     * @param cidrBlock the CIDR notation string
     * @param <A>       the address type
     * @return block instance
     * @throws ParseException on invalid expression or wrong IP version
     */
    @SuppressWarnings("unchecked")
    public static <A extends Addr<A>> Block<A> parseCidr(Family<A> family, String cidrBlock) {
        Block<?> actual = parseCidr(cidrBlock);
        validate(family == actual.first().family(), "Wrong IP type", actual, ParseException::new);
        return (Block<A>) actual;
    }

    /**
     * {@link Collector} for creating {@link AddressSet} from {@link Stream}.
     *
     * @return collector
     * @param <A> address family
     */
    public static <A extends Addr<A>> Collector<AddressSet<A>, Collection<AddressSet<A>>, AddressSet<A>> collector() {
        return AddressSetCollector.impl();
    }

    private static final class Empty<A extends Addr<A>> extends AbstractAddressSet<A> {
        static final AddressSet<?> IMPL = new Empty<>();

        @Override
        public boolean contains(Addr<?> address) {
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

    private static final class ArraySet<A extends Addr<A>> extends AbstractAddressSet<A> {
        private final Range<A>[] ranges;

        ArraySet(Range<A>[] ranges) {
            this.ranges = ranges;
        }

        @Override
        public Stream<Range<A>> ranges() {
            return Stream.of(ranges);
        }

        @Override
        public boolean contains(Addr<?> address) {
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

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public String toString() {
            int LIMIT = 5;
            StringJoiner buf = new StringJoiner(", ", "{", "}");
            for (int i = 0; i < Math.min(ranges.length, LIMIT); i++) {
                buf.add(ranges[i].toString());
            }
            if (ranges.length > LIMIT) {
                buf.add("[" + ranges.length + "...]");
            }
            return buf.toString();
        }
    }

}
