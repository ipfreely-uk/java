package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;

import java.util.Collections;
import java.util.Objects;

/**
 * Unicast IP address registry for IPv6.
 */
public final class Unicast {
    private static final RegistrySet<V4> EMPTY = new RegistrySet<>("IANA does not publish IPv4 unicast registry", "n/a", Collections.emptyList());

    private Unicast() {}

    /**
     * <p>
     *     <a href="https://www.iana.org/assignments/ipv6-unicast-address-assignments/ipv6-unicast-address-assignments.xml"
     *     >IPv6 Global Unicast Address Assignments</a>.
     * </p>
     * <p>
     *     IANA does not publish an equivalent table for IPv4.
     *
     * </p>
     *
     * @param f IPv4 or IPv6
     * @return the registry
     * @param <A> address family
     */
    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        return Objects.requireNonNull(f) == Family.v4()
                ? (RegistrySet<A>) EMPTY
                : (RegistrySet<A>) RegistryParserUnicastIPv6.REG;
    }
}
