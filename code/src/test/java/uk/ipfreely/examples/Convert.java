// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.examples;

import uk.ipfreely.Addr;
import uk.ipfreely.Family;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility type for converting between {@link Addr} and {@link InetAddress}.
 */
public final class Convert {

    private Convert() {}

    public static InetAddress toInetAddress(Addr<?> address) {
        try {
            return InetAddress.getByAddress(address.toBytes());
        } catch (UnknownHostException e) {
            // unreachable - getByAddress only throws on illegal byte len
            throw new AssertionError(e);
        }
    }

    public static Addr<?> toAddress(InetAddress address) {
        return Family.unknown(address.getAddress());
    }
}
