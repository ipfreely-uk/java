// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.sets;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.ParseException;

import java.math.BigInteger;
import java.util.*;
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
    public static <A extends Address<A>, S extends AddressSet<A>> AddressSet<A> of(S... sets) {
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
    public static <A extends Address<A>, S extends AddressSet<A>> AddressSet<A> from(Iterable<S> sets) {
        List<Range<A>> list = new ArrayList<>();
        for (S set : sets) {
            set.ranges().forEach(list::add);
        }
        final Range<A>[] data = rationalize(list);
        if (data.length == 0) {
            return (AddressSet<A>) Empty.IMPL;
        }
        if (data.length == 1) {
            return data[0];
        }
        return new ArraySet<>(data);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Address<A>, R extends Range<A>> Range<A>[] rationalize(Iterable<R> ranges) {
        SortedSet<Range<A>> set = new TreeSet<>(AddressSets::compare);
        rationalize(set, ranges);
        return set.toArray(new Range[0]);
    }

    private static <A extends Address<A>, R extends Range<A>> void rationalize(SortedSet<Range<A>> set, Iterable<R> ranges) {
        for (Range<A> range : ranges) {
            Iterator<Range<A>> it = set.iterator();
            while(it.hasNext()) {
                Range<A> candidate = it.next();
                if (cannotCombine(candidate, range)) {
                    break;
                }
                if (range.contiguous(candidate)) {
                    it.remove();
                    range = range.combine(candidate);
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

    /**
     * Single address as {@link Block}.
     *
     * @param address IP
     * @param <A> IP version
     * @return immutable instance
     */
    public static <A extends Address<A>> Block<A> address(final A address) {
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
    public static <A extends Address<A>> Block<A> block(final A first, final int maskSize) {
        final Family<A> family = first.family();
        int width = family.width();
        if (maskSize == width) {
            return address(first);
        }

        validate(maskSize >= 0, "Valid mask size cannot be less than 0", maskSize, IllegalArgumentException::new);
        validate(maskSize <= width, "Mask must not exceed address width 32 (IPv4) or 128 (IPv6)", maskSize, IllegalArgumentException::new);

        // TODO: mask and first check

        final List<A> masks = family.subnets().masks();
        final A zero = family.min();
        final A inverseMask = masks.get(maskSize).not();
        final A anded = first.and(inverseMask);

        final A last = first.or(inverseMask);
        final Block<A> block = block(first, last);

        // TODO: check mask bits before this
        validate(zero.equals(anded), "Mask must cover all address bits", block, IllegalArgumentException::new);

        return block;
    }

    /**
     * <p>
     *     Creates a block from the given addresses which MUST form a valid CIDR block.
     * </p>
     *
     * @param first address
     * @param last address which must be greater or equal to the first address
     * @param <A> IP version
     * @return block
     */
    public static <A extends Address<A>> Block<A> block(final A first, final A last) {
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
    public static <A extends Address<A>> Range<A> range(A first, A last) {
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

        if (first.equals(last)) {
            return address(first);
        }

        validate(first.compareTo(last) <= 0, "First address must be less than or equal to last", first, IllegalArgumentException::new);

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
    @SuppressWarnings({"unchecked"})
    public static Block<?> parseCidr(String cidrBlock) {
        final int stroke = cidrBlock.lastIndexOf('/');
        validate(stroke >= 0, "CIDR notation is 'ip/mask'", cidrBlock, ParseException::new);

        final String s1 = cidrBlock.substring(0, stroke);
        final String s2 = cidrBlock.substring(stroke + 1);

        Address address = Family.unknown(s1);
        int mask = Integer.parseInt(s2);
        try {
            return block(address, mask);
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
    public static <A extends Address<A>> Block<A> parseCidr(Family<A> family, String cidrBlock) {
        Block<?> actual = parseCidr(cidrBlock);
        validate(family == actual.first().family(), "Wrong IP type", actual, ParseException::new);
        return (Block<A>) actual;
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

}
