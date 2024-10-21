package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.util.Objects;

/**
 * <p>Multicast IP address registry static methods.</p>
 */
public final class Multicast {
    private Multicast() {}

    /**
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
     *
     * @param f IPv4 or IPv6
     * @return registry
     * @param <A> address family
     */
    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (RegistrySet<A>) RegistryParserMulticastIPv4.REG
                : (RegistrySet<A>) RegistryParserMulticastIPv6.REG;
    }
}
