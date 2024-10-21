package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import javax.xml.xpath.XPathExpressionException;

final class RegistryParserSpecialIPv6 extends RegistryParserSpecial<V6> {
    static final RegistrySet<V6> REG = new RegistryParserSpecialIPv6().load(IanaIpv6SpecialRegistry.bytes());

    private RegistryParserSpecialIPv6() {}

    @Override
    AddressSet<V6> addresses(Node record) throws XPathExpressionException {
        String a = address.evaluate(record).trim();
        return AddressSets.parseCidr(Family.v6(), a);
    }
}
