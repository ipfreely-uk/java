package uk.ipfreely.sets;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;

import static java.util.Objects.requireNonNull;
import static uk.ipfreely.Family.v4;
import static uk.ipfreely.sets.AddressSets.parseCidr;
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

    // TODO: singletons

    /**
     * Loopback addresses.
     * Includes the localhost address.
     *
     * @param f IP family
     * @return loopback addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> loopback(Family<A> f) {
        var expr = (requireNonNull(f) == v4()) ? "127.0.0.0/8" : "::1/128";
        return parseCidr(f, expr);
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
            var ten = parseCidr(f, "10.0.0.0/8");
            var oneSevenTwo = parseCidr(f, "172.16.0.0/12");
            var oneNineTwo = parseCidr(f, "192.168.0.0/16");
            return AddressSets.of(ten, oneSevenTwo, oneNineTwo);
        }
        if (inUse) {
            return parseCidr(f, "FD00::/8");
        }
        return parseCidr(f, "FC00::/7");
    }

    /**
     * Link-local addresses for use in subnets.
     *
     * @param f IP family
     * @return link-local addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> linkLocal(Family<A> f) {
        var expr = (requireNonNull(f) == v4()) ? "169.254.0.0/16" : "fe80::/10";
        return parseCidr(f, expr);
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
            var oneNineTwo = parseCidr(f, "192.0.2.0/24");
            var oneNineEight = parseCidr(f, "198.51.100.0/24");
            var twoOhThree = parseCidr(f, "203.0.113.0/24");
            return AddressSets.of(oneNineTwo, oneNineEight, twoOhThree);
        }
        var twoThouOne = parseCidr(f, "2001:db8::/32");
        var threeFs = parseCidr(f, "3fff::/20");
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
            var first = f.parse("224.0.0.0");
            var last = f.parse("239.255.255.255");
            return range(first, last);
        }
        return parseCidr(f, "ff00::/8");
    }
}
