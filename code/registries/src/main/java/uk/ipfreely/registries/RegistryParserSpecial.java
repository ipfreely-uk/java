package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Address;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.EnumMap;
import java.util.Map;

abstract class RegistryParserSpecial<A extends Address<A>> extends RegistryParser<A> {
    final XPathExpression address = exp("a:address");
    private final XPathExpression source = exp("a:source");
    private final XPathExpression destination = exp("a:destination");
    private final XPathExpression forwardable = exp("a:forwardable");
    private final XPathExpression global = exp("a:global");
    private final XPathExpression reserved = exp("a:reserved");

    @Override
    Map<Special.Routing, Boolean> rules(Node record) throws XPathExpressionException {
        EnumMap<Special.Routing, Boolean> em = new EnumMap<>(Special.Routing.class);
        set(em, record, source, Special.Routing.SOURCE);
        set(em, record, destination, Special.Routing.DESTINATION);
        set(em, record, forwardable, Special.Routing.FORWARDABLE);
        set(em, record, global, Special.Routing.GLOBALLY_REACHABLE);
        set(em, record, reserved, Special.Routing.RESERVED_BY_PROTOCOL);
        return em;
    }

    private void set(Map<Special.Routing, Boolean> map, Node record, XPathExpression exp, Special.Routing key) throws XPathExpressionException {
        String value = exp.evaluate(record);
        if (value.isEmpty()) {
            return;
        }
        if (value.contains("True")) {
            map.put(key, true);
        }
        if (value.contains("False")) {
            map.put(key, false);
        }
    }

    @Override
    String recordDescription() {
        return "a:name";
    }
}
