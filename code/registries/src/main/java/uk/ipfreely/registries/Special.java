package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *     Special-purpose IP address registry static methods.
 * </p>
 */
public final class Special {
    private Special() {}

    /**
     * <p>
     *     Special-purpose IP address records for a given IP address family.
     * </p>
     * <p>
     *     Special records may intersect.
     *     "This network" <code>0.0.0.0/8</code> contains
     *     "This host on this network" <code>0.0.0.0/32</code>.
     * </p>
     * <ul>
     *     <li>
     *         <a href="https://www.iana.org/assignments/iana-ipv4-special-registry/iana-ipv4-special-registry.xml"
     *         >IANA IPv4 Special-Purpose Address Registry</a>
     *     </li>
     *     <li>
     *         <a href="https://www.iana.org/assignments/iana-ipv6-special-registry/iana-ipv6-special-registry.xml"
     *         >IANA IPv6 Special-Purpose Address Registry</a>
     *     </li>
     * </ul>
     *
     * @param f IPv4 or IPv6
     * @return special address registry
     * @param <A> address family
     */
    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (RegistrySet<A>) RegistryParserSpecialIPv4.REG
                : (RegistrySet<A>) RegistryParserSpecialIPv6.REG;
    }

    /**
     * <p>
     *     Any routing rules associated with special addresses.
     * </p>
     * <p>
     *     Returns the empty set for records from other registries.
     * </p>
     *
     * @param r record instance
     * @return routing rules
     */
    public static Map<Routing, Boolean> rules(Record<?> r) {
        return r.routing();
    }

    /**
     * Routing rules for special-purpose IP addresses.
     */
    public enum Routing {
        /** Can be used as source address */
        SOURCE,
        /** Can be used as destination address */
        DESTINATION,
        /** Can be forwarded by router */
        FORWARDABLE,
        /** Can be forwarded beyond administrative domain */
        GLOBALLY_REACHABLE,
        /** Requires special handling */
        RESERVED_BY_PROTOCOL
    }
}
