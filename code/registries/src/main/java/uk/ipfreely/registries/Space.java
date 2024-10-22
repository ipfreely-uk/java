package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.util.Objects;

/**
 * IP address spaces.
 */
public final class Space {
    private Space() {}

    /**
     * <p>
     *
     * </p>
     * <ul>
     *     <li>
     *         <a href="https://www.iana.org/assignments/ipv4-address-space/ipv4-address-space.xml"
     *         >IANA IPv4 Address Space Registry</a>
     *     </li>
     *     <li>
     *         <a href="https://www.iana.org/assignments/ipv6-address-space/ipv6-address-space.xml"
     *         >Internet Protocol Version 6 Address Space</a>
     *     </li>
     * </ul>
     *
     * @param f IPv4 or IPv6
     * @return registry
     * @param <A> address family
     */
    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> registry(Family<A> f) {
        return Objects.requireNonNull(f) == Family.v4()
                ? (RegistrySet<A>) RegistryParserSpaceIPv4.REG
                : (RegistrySet<A>) RegistryParserSpaceIPv6.REG;
    }
}
