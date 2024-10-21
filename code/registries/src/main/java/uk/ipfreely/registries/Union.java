package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

public interface Union<A extends Address<A>> {
    AddressSet<A> addresses();
}
