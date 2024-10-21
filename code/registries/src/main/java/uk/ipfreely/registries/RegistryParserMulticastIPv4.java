package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

final class RegistryParserMulticastIPv4 extends RegistryParserMulticast<V4> {
    static final RegistrySet<V4> REG = new RegistryParserMulticastIPv4().load(MulticastAddresses.bytes());

    private final XPathExpression relative = exp("a:relative");

    @Override
    AddressSet<V4> scope(Node record) throws XPathExpressionException {
        String n = relative.evaluate(record).trim();
        if (n.contains("-")) {
            String[] range = n.split("-");
            V4 first = Family.v4().parse(Integer.parseInt(range[0]));
            V4 last = Family.v4().parse(Integer.parseInt(range[1]));
            return AddressSets.range(first, last);
        }
        V4 a = Family.v4().parse(Integer.parseInt(n));
        return AddressSets.address(a);
    }

    @Override
    AddressSet<V4> parse(String address) {
        if (address.contains("-")) {
            String[] range = address.split("-");
            V4 first = Family.v4().parse(range[0]);
            V4 last = Family.v4().parse(range[1]);
            return AddressSets.range(first, last);
        }
        V4 a = Family.v4().parse(address);
        return AddressSets.address(a);
    }
}

