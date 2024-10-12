package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *     Special IP address registries.
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
 * <p>
 *     Special record blocks may intersect.
 *     "This network" <code>0.0.0.0/8</code> contains
 *     "This host on this network" <code>0.0.0.0/32</code>.
 * </p>
 */
public final class Special {
    private Special() {}

    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (RegistrySet<A>) F4.R
                : (RegistrySet<A>) F6.R;
    }

    /**
     * Any routing rules associated with special addresses.
     *
     * @param r record instance
     * @return routing rules
     */
    public static Map<Routing, Boolean> rules(Record<?> r) {
        return r.routing();
    }

    private static final class F4 {
        static final RegistrySet<V4> R = new Xml().load(Family.v4(), IanaIpv4SpecialRegistry.bytes());
    }

    private static final class F6 {
        static final RegistrySet<V6> R = new Xml().load(Family.v6(), IanaIpv6SpecialRegistry.bytes());
    }

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
