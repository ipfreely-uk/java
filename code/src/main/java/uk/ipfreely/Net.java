// Copyright 2025 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * <p>
 *     Convenience methods for converting between {@link Addr} and {@link InetAddress}.
 * </p>
 */
public final class Net {
    private Net() {}

    /**
     * Any IP address.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static InetAddress toInetAddress(Addr<?> address) {
        Objects.requireNonNull(address);
        try {
            return InetAddress.getByAddress(address.toBytes());
        } catch (UnknownHostException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * IPv4.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static Inet4Address toInet4Address(V4 address) {
        return (Inet4Address) toInetAddress(address);
    }

    /**
     * IPv6.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static Inet6Address toInet6Address(V6 address) {
        return (Inet6Address) toInetAddress(address);
    }

    /**
     * Any IP address.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static Addr<?> toAddr(InetAddress address) {
        return Family.unknown(address.getAddress());
    }

    /**
     * IPv4.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static V4 toV4(Inet4Address address) {
        return Family.v4().parse(address.getAddress());
    }

    /**
     * IPv6.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static V6 toV6(Inet6Address address) {
        return Family.v6().parse(address.getAddress());
    }
}
