package uk.ipfreely.mvn.plugin.iana.gen;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.autores.ByteArrays;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ByteArrays(
        value = {
                "/iana-ipv4-special-registry.xml",
                "/iana-ipv6-special-registry.xml",
                "/multicast-addresses.xml",
                "/ipv6-multicast-addresses.xml",
        },
        isPublic = true
)
public final class Xml {
    private final XPath xp = xPath();
    private final XPathExpression registry = exp(xp, "a:registry");
    private final XPathExpression title = exp(xp, "a:title");
    private final XPathExpression id = exp(xp, "@id");
    private final XPathExpression record = exp(xp, "a:record");
    private final XPathExpression address = exp(xp, "a:address");
    private final XPathExpression name = exp(xp, "a:name");

    public Xml() {}

    private static XPath xPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xp = factory.newXPath();
        NamespaceContext ns = new NameSpaces("a", "http://www.iana.org/assignments");
        xp.setNamespaceContext(ns);
        return xp;
    }

    private XPathExpression exp(XPath xp, String expression) {
        try {
            return xp.compile(expression);
        } catch (XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    private Document parse(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            return (Document) xp.evaluate("/", new InputSource(in), XPathConstants.NODE);
        } catch (IOException | XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    public Map<String, Object> special(byte[] data) {
        Map<String, Object> map = newMap();
        Document doc = parse(data);
        try {
            Node reg = (Node) registry.evaluate(doc, XPathConstants.NODE);
            map.put(RegistryConstants.TITLE, title.evaluate(reg));
            map.put(RegistryConstants.ID, id.evaluate(reg));
            NodeList regs = (NodeList) registry.evaluate(reg, XPathConstants.NODESET);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0, len = regs.getLength(); i < len; i++) {
                Node r = regs.item(i);
                list.add(registry(r));
            }
            map.put(RegistryConstants.REGISTRIES, list);
        } catch (XPathExpressionException e) {
            throw new AssertionError(e);
        }
        return map;
    }

    private Map<String, Object> registry(Node registry) throws XPathExpressionException {
        Map<String, Object> map = newMap();
        map.put(RegistryConstants.TITLE, title.evaluate(registry));
        map.put(RegistryConstants.ID, id.evaluate(registry));
        NodeList records = (NodeList) record.evaluate(registry, XPathConstants.NODESET);
        List<Map<String, String>> list = new ArrayList<>();
        for (int i=0, len=records.getLength(); i < len; i++) {
            Node record = records.item(i);
            list.add(record(record));
        }
        map.put(RegistryConstants.RECORDS, list);
        return map;
    }

    private Map<String, String> record(Node record) throws XPathExpressionException {
        Map<String, String> map = newMap();
        map.put(RegistryConstants.ADDRESS, address.evaluate(record));
        map.put(RegistryConstants.NAME, name.evaluate(record));
        return map;
    }

    private static <V> Map<String, V> newMap() {
        return new LinkedHashMap<>();
    }
}
