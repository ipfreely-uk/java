package uk.ipfreely.registries;

import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

public class RegistryParserSpaceIPv4 extends RegistryParserSpace<V4> {
    static final RegistrySet<V4> REG = new RegistryParserSpaceIPv4().load(Ipv4AddressSpace.bytes());

    @Override
    AddressSet<V4> parse(String s) {
        String[] components = s.split("/");
        int bits = Integer.parseInt(components[1]);
        String cidr = components[0] + ".0.0.0/" + bits;
        return AddressSets.parseCidr(Family.v4(), cidr);
    }

    @Override
    String recordDescription() {
        return "a:designation";
    }

    @Override
    boolean isFlat() {
        return true;
    }
}
