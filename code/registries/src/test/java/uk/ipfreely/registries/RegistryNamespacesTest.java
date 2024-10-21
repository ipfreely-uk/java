package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import static org.junit.jupiter.api.Assertions.*;

class RegistryNamespacesTest {

    private final NamespaceContext nc = new RegistryParser.RegistryNamespaces();

    @Test
    void getNamespaceURI() {
        {
            String expected = XMLConstants.NULL_NS_URI;
            String actual = nc.getNamespaceURI("");
            assertEquals(expected, actual);
        }
        {
            String expected = "http://www.iana.org/assignments";
            String actual = nc.getNamespaceURI("a");
            assertEquals(expected, actual);
        }
        {
            String expected = XMLConstants.XML_NS_URI;
            String actual = nc.getNamespaceURI(XMLConstants.XML_NS_PREFIX);
            assertEquals(expected, actual);
        }
        {
            String expected = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            String actual = nc.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE);
            assertEquals(expected, actual);
        }
    }

    @Test
    void getPrefix() {
        {
            String expected = RegistryParser.PREFIX;
            String actual = nc.getPrefix("http://www.iana.org/assignments");
            assertEquals(expected, actual);
        }
    }

    @Test
    void getPrefixes() {
        {
            boolean actual = nc.getPrefixes(XMLConstants.XML_NS_URI).hasNext();
            assertTrue(actual);
        }
    }
}