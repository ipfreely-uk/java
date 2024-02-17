package uk.ipfreely.collections;

import uk.ipfreely.Address;

import java.math.BigInteger;

/**
 * Base {@link Address} collection type.
 *
 * @param <A> address type
 */
public interface AddressCollection<A extends Address<A>> extends Iterable<A> {

    /**
     * Collection size.
     *
     * @return number of addresses in collection
     */
    BigInteger size();

    /**
     * Tests membership.
     *
     * @param address candidate
     * @return true if candidate in collection
     */
    boolean contains(Address<?> address);

    /**
     * Informational only.
     *
     * @return string form
     */
    String toString();
}
