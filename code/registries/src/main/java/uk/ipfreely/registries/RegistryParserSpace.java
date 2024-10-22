package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

abstract class RegistryParserSpace<A extends Address<A>> extends RegistryParser<A> {
    private final XPathExpression prefix = exp("a:prefix");

    @Override
    AddressSet<A> addresses(Node record) throws XPathExpressionException {
        return parse(prefix.evaluate(record));
    }

    abstract AddressSet<A> parse(String s);
}
