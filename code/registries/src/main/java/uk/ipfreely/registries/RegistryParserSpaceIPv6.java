package uk.ipfreely.registries;

import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

public class RegistryParserSpaceIPv6 extends RegistryParserSpace<V6> {
    static final RegistrySet<V6> REG = new RegistryParserSpaceIPv6().load(Ipv6AddressSpace.bytes());

    @Override
    AddressSet<V6> parse(String s) {
        return AddressSets.parseCidr(Family.v6(), s);
    }

    @Override
    String recordDescription() {
        return "a:description";
    }

    @Override
    boolean isFlat() {
        return false;
    }
}
