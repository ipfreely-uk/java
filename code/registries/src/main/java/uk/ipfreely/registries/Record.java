package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import java.util.Map;

/**
 * Registry record type.
 *
 * @param <A> address family
 */
public final class Record<A extends Address<A>> implements Union<A> {
    private final String name;
    private final AddressSet<A> addresses;
    private final Map<Special.Routing, Boolean> routing;

    Record(String name, AddressSet<A> addresses, Map<Special.Routing, Boolean> routing) {
        this.name = name;
        this.addresses = addresses;
        this.routing = Colls.immutable(routing);
    }

    /**
     * Natural language name.
     *
     * @return name
     */
    public String name() {
        return name;
    }

    Map<Special.Routing, Boolean> routing() {
        return routing;
    }

    /**
     * Informational.
     *
     * @return name
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public AddressSet<A> addresses() {
        return addresses;
    }
}
