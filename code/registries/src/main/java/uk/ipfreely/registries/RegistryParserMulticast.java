package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

abstract class RegistryParserMulticast<A extends Address<A>> extends RegistryParser<A> {
    private final XPathExpression addr = exp("a:addr");

    @Override
    AddressSet<A> addresses(Node record) throws XPathExpressionException {
        String addresses = addr.evaluate(record).trim();
        if (addresses.isEmpty()) {
            return scope(record);
        }
        return parse(addresses);
    }

    abstract AddressSet<A> scope(Node record) throws XPathExpressionException;

    abstract AddressSet<A> parse(String address);
}
