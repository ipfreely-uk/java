package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import java.util.Map;

public interface Record<A extends Address<A>> {

    String name();

    AddressSet<A> set();

    Map<Unicast, Boolean> routing();
}
