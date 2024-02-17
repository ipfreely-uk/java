package uk.ipfreely.collections;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.ParseException;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static uk.ipfreely.collections.Validation.validate;

/**
 * <p>
 *     Utility type for creating {@link Range} and {@link Block} instances.
 * </p>
 */
public final class Ranges {

    // TODO: guarded ranges - protect against infinite iteration

    private Ranges() {}

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
            public int maskBits() {
                return first().family().bitWidth();
            }
        }

        return new Single();
    }

    /**
     * <p>
     *     Creates a block from and network address and mask size.
     *     The {@code maskSize} must be greater or equal to zero
     *     and less than or equal to {@link Family#bitWidth()}
     *     and the mask must cover all the true bits of the address.
     * </p>
     *
     * @param first    the first IP in the block
     * @param maskSize the number of mask bits
     * @param <A>      the Ip type
     * @return the block instance
     */
    public static <A extends Address<A>> Block<A> block(final A first, final int maskSize) {
        final Family<A> family = first.family();
        int width = family.bitWidth();
        if (maskSize == width) {
            return address(first);
        }

        validate(maskSize >= 0, "Valid mask size cannot be less than 0", maskSize, IllegalArgumentException::new);
        validate(maskSize <= width, "Mask must not exceed address width 32 (IPv4) or 128 (IPv6)", maskSize, IllegalArgumentException::new);

        // TODO: mask and first check

        final List<A> masks = family.masks();
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
        }

        Block<A> block = new AddressBlock();
        validate(first.family().maskBitsForBlock(first, last) >= 0, "Not an IP block", block, IllegalArgumentException::new);
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
    public static <A extends Address<A>> Range<A> from(A first, A last) {
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

        int maskSize = first.family().maskBitsForBlock(first, last);
        return (maskSize < 0) ? new AddressRange() : block(first, last);
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

        Address address = Family.parseUnknown(s1);
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

    /**
     * Creates a single {@link Range} from two intersecting or adjacent ranges.
     *
     * @param r0 a range
     * @param r1 another range
     * @return combined range if possible
     * @param <A> address type
     */
    public static <A extends Address<A>> Optional<Range<A>> combine(Range<A> r0, Range<A> r1) {
        A first;
        A last;
        if (r0.contains(r1.first()) || r0.contains(r1.last()) || r1.contains(r0.first()) || r1.contains(r0.last())) {
            first = Compare.least(r0.first(), r1.first());
            last = Compare.greatest(r0.last(), r1.last());
        } else if (Compare.less(r0.last(), r1.first()) && adjacent(r0.last(), r1.first())) {
            first = r0.first();
            last = r1.last();
        } else if (adjacent(r1.last(), r0.first())) {
            first = r1.first();
            last = r0.last();
        } else {
            return Optional.empty();
        }
        if (first.equals(r0.first()) && last.equals(r0.last())) {
            return Optional.of(r0);
        }
        if (first.equals(r1.first()) && last.equals(r1.last())) {
            return Optional.of(r1);
        }
        return Optional.of(Ranges.from(first, last));
    }

    private static <A extends Address<A>> boolean adjacent(A a0, A a1) {
        return Compare.less(a0, a1)
                ? a0.next().equals(a1)
                : a1.next().equals(a0);
    }

    /**
     * {@link Block} version of {@link #guarded(Range, Address)}.
     *
     * @param block block to guard
     * @param guard guard value
     * @return guarded block if block exceeds guard
     * @param <A> address type
     */
    public static <A extends Address<A>> Block<A> guarded(Block<A> block, A guard) {
        if (block instanceof GuardedBlock && ((GuardedBlock<A>) block).guard.equals(guard)) {
            return block;
        }
        if (Compare.lessOrEqual(block.last().subtract(block.first()), guard)) {
            return block;
        }
        return new GuardedBlock<>(block.first(), block.last(), guard);
    }

    /**
     * <p>
     *     Guards {@link Range} against excessive iteration.
     *     See {@link ExcessiveIterationException} for details.
     * </p>
     * <p>
     *     If {@code range.last().subtract(range.first())} is less than or equal to the guard the range
     *     returned unguarded.
     * </p>
     *
     * @param range range to guard
     * @param guard guard value
     * @return guarded range if block exceeds range
     * @param <A> address type
     */
    public static <A extends Address<A>> Range<A> guarded(Range<A> range, A guard) {
        if (range instanceof GuardedRange && ((GuardedRange<A>) range).guard.equals(guard)) {
            return range;
        }
        if (Compare.lessOrEqual(range.last().subtract(range.first()), guard)) {
            return range;
        }
        if (range instanceof Block) {
            return guarded((Block<A>) range, guard);
        }
        return new GuardedBlock<>(range.first(), range.last(), guard);
    }

    private static class GuardedRange<A extends Address<A>> extends AbstractRange<A> {
        private final A first;
        private final A last;
        final A guard;

        GuardedRange(A first, A last, A guard) {
            this.first = first;
            this.last = last;
            this.guard = guard;
        }

        @Override
        public A first() {
            return first;
        }

        @Override
        public A last() {
            return last;
        }

        @Override
        public Iterator<A> iterator() {
            return new GuardedIterator<>(first, last, guard);
        }

        @Override
        public Spliterator<A> spliterator() {
            return new GuardedSpliterator<>(first, last, guard);
        }

        @Override
        public Stream<A> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override
        public Stream<Block<A>> blocks() {
            Spliterator<Block<A>> s = new BlockSpliterator<>(first, last);
            Spliterator<Block<A>> gs = new GuardedGeneralSpliterator<>(s, this.guard, this::guardedBlock);
            return StreamSupport.stream(gs, false);
        }

        private Block<A> guardedBlock(Block<A> b) {
            return new GuardedBlock<>(b.first(), b.last(), guard);
        }
    }

    private static final class GuardedBlock<A extends Address<A>> extends GuardedRange<A> implements Block<A> {

        GuardedBlock(A first, A last, A guard) {
            super(first, last, guard);
        }

        @Override
        public Stream<Block<A>> blocks() {
            return Stream.of(this);
        }
    }
}
