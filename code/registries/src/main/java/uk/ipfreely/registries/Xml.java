package uk.ipfreely.registries;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.autores.ByteArrays;
import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.sets.AddressSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

@ByteArrays(
        {
                "iana-ipv4-special-registry.xml",
                "iana-ipv6-special-registry.xml",
                "multicast-addresses.xml",
                "ipv6-multicast-addresses.xml",
        }
)
final class Xml {
    private final XPath xp = xPath();
    private final XPathExpression registry = exp(xp, "a:registry");
    private final XPathExpression title = exp(xp, "a:title");
    private final XPathExpression id = exp(xp, "@id");
    private final XPathExpression record = exp(xp, "a:record");
    private final XPathExpression address = exp(xp, "a:address");
    private final XPathExpression addr = exp(xp, "a:addr");
    private final XPathExpression relative = exp(xp, "a:relative");
    private final XPathExpression value = exp(xp, "a:value");
    private final XPathExpression name = exp(xp, "a:name");
    private final XPathExpression source = exp(xp, "a:source");
    private final XPathExpression destination = exp(xp, "a:destination");
    private final XPathExpression forwardable = exp(xp, "a:forwardable");
    private final XPathExpression global = exp(xp, "a:global");
    private final XPathExpression reserved = exp(xp, "a:reserved");

    Xml() {}

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

    <A extends Address<A>> RegistrySet<A> load(Family<A> f, byte[] data) {
        Document doc = parse(data);
        try {
            Node reg = (Node) registry.evaluate(doc, XPathConstants.NODE);
            NodeList regs = (NodeList) registry.evaluate(reg, XPathConstants.NODESET);
            List<RecordSet<A>> list = new ArrayList<>();
            for (int i = 0, len = regs.getLength(); i < len; i++) {
                Node r = regs.item(i);
                list.add(registry(f, r));
            }
            String t = title.evaluate(reg);
            String i = id.evaluate(reg);
            return new RegistrySet<>(t, i, list);
        } catch (XPathExpressionException e) {
            throw new AssertionError(e);
        }
    }

    private <A extends Address<A>> RecordSet<A> registry(Family<A> f, Node registry) throws XPathExpressionException {
        String t = title.evaluate(registry);
        String ident = id.evaluate(registry);
        NodeList records = (NodeList) record.evaluate(registry, XPathConstants.NODESET);
        List<Record<A>> list = new ArrayList<>();
        for (int i=0, len=records.getLength(); i < len; i++) {
            Node record = records.item(i);
            list.add(record(ident, f, record));
        }
        return new RecordSet<>(t, ident, list);
    }

    private <A extends Address<A>> Record<A> record(String id, Family<A> f, Node record) throws XPathExpressionException {
        AddressSet<A> addresses;
        String a = address.evaluate(record);
        if (a == null || a.isEmpty()) {
            a = addr.evaluate(record);
            if (a == null || a.isEmpty()) {
                a = relative.evaluate(record);
                if (a == null || a.isEmpty()) {
                    a = value.evaluate(record);
                    addresses = Addressing.rel(id, f, a);
                } else {
                    addresses = Addressing.rel(id, f, a);
                }
            } else {
                addresses = Addressing.multi(id, f, a);
            }
        } else {
            addresses = Addressing.parse(id, f, a);
        }
        String n = name.evaluate(record);
        Map<Special.Routing, Boolean> rules = rules(record);
        return new Record<>(n, addresses, rules);
    }

    private Map<Special.Routing, Boolean> rules(Node record) throws XPathExpressionException {
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
        if (nullOrEmpty(value)) {
            return;
        }
        if (value.contains("True")) {
            map.put(key, true);
        }
        if (value.contains("False")) {
            map.put(key, false);
        }
    }

    private static boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private static final class NameSpaces implements NamespaceContext {

        private final Map<String, String> prefixMap;
        private final Map<String, Set<String>> nsMap;

        NameSpaces(
                Map<String, String> prefixMappings) {
            prefixMap = createPrefixMap(prefixMappings);
            nsMap = createNamespaceMap(prefixMap);
        }

        NameSpaces(String... mappingPairs) {
            this(toMap(mappingPairs));
        }

        private static Map<String, String> toMap(
                String... mappingPairs) {
            Map<String, String> prefixMappings = new HashMap<String, String>(
                    mappingPairs.length / 2);
            for (int i = 0; i < mappingPairs.length; i++) {
                prefixMappings
                        .put(mappingPairs[i], mappingPairs[++i]);
            }
            return prefixMappings;
        }

        private Map<String, String> createPrefixMap(
                Map<String, String> prefixMappings) {
            Map<String, String> prefixMap = new HashMap<String, String>(
                    prefixMappings);
            addConstant(prefixMap, XMLConstants.XML_NS_PREFIX,
                    XMLConstants.XML_NS_URI);
            addConstant(prefixMap, XMLConstants.XMLNS_ATTRIBUTE,
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
            return Collections.unmodifiableMap(prefixMap);
        }

        private void addConstant(Map<String, String> prefixMap,
                                 String prefix, String nsURI) {
            String previous = prefixMap.put(prefix, nsURI);
            if (previous != null && !previous.equals(nsURI)) {
                throw new IllegalArgumentException(prefix + " -> "
                        + previous + "; see NamespaceContext contract");
            }
        }

        private Map<String, Set<String>> createNamespaceMap(
                Map<String, String> prefixMap) {
            Map<String, Set<String>> nsMap = new HashMap<String, Set<String>>();
            for (Map.Entry<String, String> entry : prefixMap
                    .entrySet()) {
                String nsURI = entry.getValue();
                Set<String> prefixes = nsMap.computeIfAbsent(nsURI, k -> new HashSet<>());
                prefixes.add(entry.getKey());
            }
            for (Map.Entry<String, Set<String>> entry : nsMap
                    .entrySet()) {
                Set<String> readOnly = Collections
                        .unmodifiableSet(entry.getValue());
                entry.setValue(readOnly);
            }
            return nsMap;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            requireNonNull(prefix);
            String nsURI = prefixMap.get(prefix);
            return nsURI == null ? XMLConstants.NULL_NS_URI : nsURI;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            requireNonNull(namespaceURI);
            Set<String> set = nsMap.get(namespaceURI);
            return set == null ? null : set.iterator().next();
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            requireNonNull(namespaceURI);
            Set<String> set = nsMap.get(namespaceURI);
            return set.iterator();
        }
    }
}
