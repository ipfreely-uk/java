package uk.ipfreely.sets;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;

import static java.util.Objects.requireNonNull;
import static uk.ipfreely.sets.AddressSets.parseCidr;

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
public final class SpecialPurpose {
    private SpecialPurpose() {}

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
        var expr = (requireNonNull(f) == Family.v4()) ? "127.0.0.0/8" : "::1/128";
        return parseCidr(f, expr);
    }

    /**
     * Addresses reserved for private networks.
     *
     * @param f IP family
     * @param inUse set to true to only return currently allocated ranges
     * @return private use addresses
     * @param <A> address family
     */
    public static <A extends Addr<A>> AddressSet<A> localUse(Family<A> f, boolean inUse) {
        if (requireNonNull(f) == Family.v4()) {
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
        var expr = (requireNonNull(f) == Family.v4()) ? "169.254.0.0/16" : "fe80::/10";
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
        if (requireNonNull(f) == Family.v4()) {
            var oneNineTwo = parseCidr(f, "192.0.2.0/24");
            var oneNineEight = parseCidr(f, "198.51.100.0/24");
            var twoOhThree = parseCidr(f, "203.0.113.0/24");
            return AddressSets.of(oneNineTwo, oneNineEight, twoOhThree);
        }
        var twoThouOne = parseCidr(f, "2001:db8::/32");
        var threeFs = parseCidr(f, "3fff::/20");
        return AddressSets.of(twoThouOne, threeFs);
    }
}
