/**
 * <p>
 *     Data derived from IANA <a href="https://www.iana.org/numbers">IP Address Allocations</a>.
 *     This library enables more complex behaviour than can be supported with
 *     methods like {@link java.net.InetAddress#isMulticastAddress()},
 *     {@link java.net.InetAddress#isSiteLocalAddress()}, etc.
 * </p>
 * <p>
 *     The module name of this library is "uk.ipfreely.registries".
 *     This module depends on the "uk.ipfreely" and "java.xml" modules.
 * </p>
 */
@ByteArrays(
        {
                "ipv4-address-space.xml",
                "ipv6-address-space.xml",
                "iana-ipv4-special-registry.xml",
                "iana-ipv6-special-registry.xml",
                "multicast-addresses.xml",
                "ipv6-multicast-addresses.xml",
                "ipv6-unicast-address-assignments.xml",
                "ipv6-anycast-addresses.xml",
        }
)
package uk.ipfreely.registries;

import uk.autores.ByteArrays;