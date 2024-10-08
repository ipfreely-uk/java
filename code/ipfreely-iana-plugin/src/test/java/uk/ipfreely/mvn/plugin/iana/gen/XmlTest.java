package uk.ipfreely.mvn.plugin.iana.gen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlTest {

    @Test
    void doc() {
        Xml xml = new Xml();
        Map<String, Object> result = xml.special(IanaIpv4SpecialRegistry.bytes());
        System.out.println(result);

        String expected = "IANA IPv4 Special-Purpose Address Registry";
        assertEquals(expected, result.get(RegistryConstants.TITLE));
    }
}