package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

final class RegistryParserUnicastIPv6 extends RegistryParser<V6> {
    static final RegistrySet<V6> REG = new RegistryParserUnicastIPv6().load(Ipv6UnicastAddressAssignments.bytes());

    private final XPathExpression addr = exp("a:prefix");

    @Override
    String recordDescription() {
        return "a:description";
    }

    @Override
    AddressSet<V6> addresses(Node record) throws XPathExpressionException {
        String addresses = addr.evaluate(record).trim();
        return AddressSets.parseCidr(Family.v6(), addresses);
    }

    @Override
    boolean isFlat() {
        return true;
    }
}
