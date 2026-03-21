package uk.ipfreely.sets;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;

import static java.util.Objects.requireNonNull;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.sets.AddressSets.range;

/**
 * Select IANA special purpose address sets.
 * <p>
 *     See
 *     <a href="https://www.iana.org/assignments/iana-ipv4-special-registry/iana-ipv4-special-registry.xml"
 *     >IPv4 Special-Purpose Address Space</a>
 *     and
 *     <a href="https://www.iana.org/assignments/iana-ipv6-special-registry/iana-ipv6-special-registry.xml"
 *     >IPv6 Special-Purpose Address Space</a>.
 * </p>
 */
public final class AddressSpaces {
    private AddressSpaces() {}

    /**
     * Loopback addresses.
     * Includes the localhost address.
     *
     * @param f IP family
     * @return loopback addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> loopback(Family<A> f) {
        return (requireNonNull(f) == v4())
                ? block(f, 127, 0, 0, 0, 8)
                : tail(f, 1, 128);
    }

    /**
     * Addresses reserved for private networks.
     * <p>
     *     <code>FD00::/8</code> is the in use range for IPv6.
     * </p>
     *
     * @param f IP family
     * @param inUse set to true to only return currently allocated ranges
     * @return private use addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> uniqueLocal(Family<A> f, boolean inUse) {
        if (requireNonNull(f) == v4()) {
            var ten = block(f, 10, 0, 0, 0, 8);
            var oneSevenTwo = block(f, 172, 16, 0, 0, 12);
            var oneNineTwo = block(f, 192, 168, 0, 0, 16);
            return AddressSets.of(ten, oneSevenTwo, oneNineTwo);
        }
        if (inUse) {
            return head(f, 0xFD00, 0, 8);
        }
        return head(f, 0xFC00, 0, 7);
    }

    /**
     * Link-local addresses for use in subnets.
     *
     * @param f IP family
     * @return link-local addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> linkLocal(Family<A> f) {
        return (requireNonNull(f) == v4())
                ? block(f, 169, 254, 0, 0, 16)
                : head(f, 0xFE80, 0, 10);
    }

    /**
     * Addresses reserved for documentation.
     *
     * @param f IP family
     * @return documentation addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> documentation(Family<A> f) {
        if (requireNonNull(f) == v4()) {
            var oneNineTwo = block(f, 192, 0, 2, 0, 24);
            var oneNineEight = block(f, 198, 51, 100, 0, 24);
            var twoOhThree = block(f, 203, 0, 113, 0, 24);
            return AddressSets.of(oneNineTwo, oneNineEight, twoOhThree);
        }
        var twoThouOne = head(f, 0x2001, 0xdb8, 32);
        var threeFs = head(f, 0x3FFF, 0, 20);
        return AddressSets.of(twoThouOne, threeFs);
    }

    /**
     * Multicast addresses.
     * <p>
     *     See
     *     <a href="https://www.iana.org/assignments/multicast-addresses/multicast-addresses.xml">IPv4
     *     Multicast Address Space</a> and
     *     <a href="https://www.iana.org/assignments/ipv6-multicast-addresses/ipv6-multicast-addresses.xml">IPv6
     *     Multicast Address Space</a>.
     * </p>
     *
     * @param f IP family
     * @return multicast addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> multicast(Family<A> f) {
        if (f == v4()) {
            var first = a4(f, 224, 0, 0, 0);
            var last = a4(f, 239, 255, 255, 255);
            return range(first, last);
        }
        return head(f, 0xFF00, 0, 8);
    }

    private static <A extends Addr<A>> A a4(Family<A> f, int n0, int n1, int n2, int n3) {
        int ip = n0 << 24 | n1 << 16 | n2 << 8 | n3;
        return f.parse(ip);
    }

    private static <A extends Addr<A>> Block<A> block(Family<A> f, int n0, int n1, int n2, int n3, int bits) {
        A ip = a4(f, n0, n1, n2, n3);
        return AddressSets.block(ip, bits);
    }

    private static <A extends Addr<A>> Block<A> tail(Family<A> f, long n, int bits) {
        A ip = f.parse(0, n);
        return AddressSets.block(ip, bits);
    }

    private static <A extends Addr<A>> Block<A> head(Family<A> f, long n0, long n1, int bits) {
        long high = n0 << 48 | n1 << 32;
        A ip = f.parse(high, 0);
        return AddressSets.block(ip, bits);
    }
}
