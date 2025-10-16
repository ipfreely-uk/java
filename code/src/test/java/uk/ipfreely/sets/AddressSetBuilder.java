package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.HashSet;
import java.util.Set;

/**
 * Mutable {@link AddressSet} builder.
 * Attempts to compact intermediate states.
 * This type is not thread-safe.
 *
 * @param <A> address family
 */
public final class AddressSetBuilder<A extends Addr<A>> {
    private final Set<AddressSet<A>> data = new HashSet<>();

    /**
     * Adds set of addresses.
     *
     * @param set addresses
     * @return this
     */
    public AddressSetBuilder<A> add(AddressSet<A> set) {
        data.add(set);
        if (data.size() >= 512) {
            var compacted = AddressSets.from(data);
            data.clear();
            data.add(compacted);
        }
        return this;
    }

    /**
     * Adds single address.
     *
     * @param address element
     * @return this
     */
    public AddressSetBuilder<A> add(A address) {
        return add(AddressSets.address(address));
    }

    /**
     * Builds the set.
     *
     * @return union of addresses
     */
    public AddressSet<A> build() {
        return AddressSets.from(data);
    }

    /**
     * Returns the builder to initial state.
     *
     * @return this
     */
    public AddressSetBuilder<A> clear() {
        data.clear();
        return this;
    }
}
