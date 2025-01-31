// Copyright 2025 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * <p>
 *     Convenience methods for converting to/from {@link InetAddress}.
 * </p>
 */
public final class Net {
    private Net() {}

    /**
     * Creates {@link InetAddress}.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static InetAddress toInetAddress(Addr<?> address) {
        try {
            return InetAddress.getByAddress(address.toBytes());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates {@link Inet4Address}.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static Inet4Address toInet4Address(V4 address) {
        return (Inet4Address) toInetAddress(address);
    }

    /**
     * Creates {@link Inet6Address}.
     *
     * @param address mathematical type
     * @return I/O type
     */
    public static Inet6Address toInet6Address(V6 address) {
        return (Inet6Address) toInetAddress(address);
    }

    /**
     * From {@link InetAddress}.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static Addr<?> toAddr(InetAddress address) {
        return Family.unknown(address.getAddress());
    }

    /**
     * From {@link Inet4Address}.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static V4 toV4(Inet4Address address) {
        return Family.v4().parse(address.getAddress());
    }

    /**
     * From {@link Inet6Address}.
     *
     * @param address I/O type
     * @return mathematical type
     */
    public static V6 toV6(Inet6Address address) {
        return Family.v6().parse(address.getAddress());
    }
}
