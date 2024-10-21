package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

/**
 * Container of IP addresses
 *
 * @param <A> address family
 */
public interface Union<A extends Address<A>> {
    /**
     * All addresses.
     *
     * @return union set
     */
    AddressSet<A> addresses();
}
