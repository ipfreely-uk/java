package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.Objects;

/**
 * <p>Multicast IP address registry static methods.</p>
 * <ul>
 *     <li>
 *         <a href="https://www.iana.org/assignments/multicast-addresses/multicast-addresses.xml"
 *         >IPv4 Multicast Address Space Registry</a>
 *     </li>
 *     <li>
 *         <a href="https://www.iana.org/assignments/ipv6-multicast-addresses/ipv6-multicast-addresses.xml"
 *         >IPv6 Multicast Address Space Registry</a>
 *     </li>
 *  </ul>
 */
public final class Multicast {
    private Multicast() {}

    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (RegistrySet<A>) F4.R
                : (RegistrySet<A>) F6.R;
    }

    private static final class F4 {
        static final RegistrySet<V4> R = new Xml().load(Family.v4(), MulticastAddresses.bytes());
    }

    private static final class F6 {
        static final RegistrySet<V6> R = new Xml().load(Family.v6(), Ipv6MulticastAddresses.bytes());
    }
}
