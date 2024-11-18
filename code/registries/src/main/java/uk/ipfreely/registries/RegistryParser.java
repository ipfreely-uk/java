package uk.ipfreely.registries;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

abstract class RegistryParser<A extends Address<A>> {
    static final String PREFIX = "a";

    private final XPath xp = xPath();
    private final XPathExpression registry = exp("a:registry");
    private final XPathExpression title = exp("a:title");
    private final XPathExpression id = exp("@id");
    private final XPathExpression record = exp("a:record");
    private XPathExpression name;

    abstract String recordDescription();

    private Record<A> record(Node record) throws XPathExpressionException {
        if (name == null) {
            name = exp(recordDescription());
        }
        String n = name.evaluate(record);
        AddressSet<A> addresses = addresses(record);
        Map<Special.Routing, Boolean> rules = rules(record);
        return new Record<>(n, addresses, rules);
    }


    private Document parse(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            return (Document) xp.evaluate("/", new InputSource(in), XPathConstants.NODE);
        } catch (IOException | XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    RegistrySet<A> load(byte[] data) {
        Document doc = parse(data);
        try {
            Node reg = (Node) registry.evaluate(doc, XPathConstants.NODE);
            List<RecordSet<A>> list = new ArrayList<>();
            if (isFlat()) {
                list.add(registry(reg));
            } else {
                NodeList regs = (NodeList) registry.evaluate(reg, XPathConstants.NODESET);
                for (int i = 0, len = regs.getLength(); i < len; i++) {
                    Node r = regs.item(i);
                    list.add(registry(r));
                }
            }
            String t = title.evaluate(reg);
            String i = id.evaluate(reg);
            return new RegistrySet<>(t, i, list);
        } catch (XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    private RecordSet<A> registry(Node registry) throws XPathExpressionException {
        String t = title.evaluate(registry);
        String ident = id.evaluate(registry);
        NodeList records = (NodeList) record.evaluate(registry, XPathConstants.NODESET);
        List<Record<A>> list = new ArrayList<>();
        for (int i=0, len=records.getLength(); i < len; i++) {
            Node record = records.item(i);
            list.add(record(record));
        }
        return new RecordSet<>(t, ident, list);
    }

    abstract AddressSet<A> addresses(Node record) throws XPathExpressionException;

    Map<Special.Routing, Boolean> rules(Node record) throws XPathExpressionException {
        return Collections.emptyMap();
    }

    XPathExpression exp(String expression) {
        try {
            return xp.compile(expression);
        } catch (XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    abstract boolean isFlat();

    private static XPath xPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xp = factory.newXPath();
        NamespaceContext ns = new RegistryNamespaces();
        xp.setNamespaceContext(ns);
        return xp;
    }

    static final class RegistryNamespaces implements NamespaceContext {
        private final Map<String, String> prefixMap;
        private final Map<String, NavigableSet<String>> nsMap;

        RegistryNamespaces() {
            prefixMap = new HashMap<>();
            prefixMap.put(PREFIX, "http://www.iana.org/assignments");
            prefixMap.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
            prefixMap.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
            nsMap = createNamespaceMap(prefixMap);
        }

        private Map<String, NavigableSet<String>> createNamespaceMap(
                Map<String, String> prefixMap) {
            Map<String, NavigableSet<String>> nsMap = new HashMap<>();
            for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
                String nsURI = entry.getValue();
                Set<String> prefixes = nsMap.computeIfAbsent(nsURI, k -> new TreeSet<>());
                prefixes.add(entry.getKey());
            }
            for (Map.Entry<String, NavigableSet<String>> entry : nsMap.entrySet()) {
                entry.setValue(entry.getValue());
            }
            return nsMap;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            requireNonNull(prefix);
            return prefixMap.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            requireNonNull(namespaceURI);
            NavigableSet<String> set = nsMap.getOrDefault(namespaceURI, Collections.emptyNavigableSet());
            return set.first();
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            requireNonNull(namespaceURI);
            Set<String> set = nsMap.get(namespaceURI);
            return Collections.unmodifiableSet(set).iterator();
        }
    }
}
