package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

public class Multicast<A extends Address<A>> implements Record<A> {


    @Override
    public String name() {
        return "";
    }

    @Override
    public AddressSet<A> set() {
        return null;
    }
}
