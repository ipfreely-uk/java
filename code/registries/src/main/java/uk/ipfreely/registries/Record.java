package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import java.util.Map;

public final class Record<A extends Address<A>> {
    private final String name;
    private final AddressSet<A> addresses;
    private final Map<Unicast, Boolean> routing;

    Record(String name, AddressSet<A> addresses, Map<Unicast, Boolean> routing) {
        this.name = name;
        this.addresses = addresses;
        this.routing = Colls.immutable(routing);
    }

    public String name() {
        return name;
    }

    public AddressSet<A> addresses() {
        return addresses;
    }

    public Map<Unicast, Boolean> routing() {
        return routing;
    }

    @Override
    public String toString() {
        return "Record{" +
                "name='" + name + '\'' +
                ", addresses=" + addresses +
                ", routing=" + routing +
                '}';
    }
}
