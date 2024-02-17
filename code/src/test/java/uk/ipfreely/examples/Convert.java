package uk.ipfreely.examples;

import uk.ipfreely.Address;
import uk.ipfreely.Family;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility type for converting between {@link Address} and {@link InetAddress}.
 */
public final class Convert {

    private Convert() {}

    public static InetAddress toInetAddress(Address<?> address) {
        try {
            return InetAddress.getByAddress(address.toBytes());
        } catch (UnknownHostException e) {
            // unreachable - getByAddress only throws on illegal byte len
            throw new AssertionError(e);
        }
    }

    public static Address<?> toAddress(InetAddress address) {
        return Family.parseUnknown(address.getAddress());
    }
}
