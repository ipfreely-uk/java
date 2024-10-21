package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;
import uk.ipfreely.sets.Block;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class RegistryParserSpecialIPv4 extends RegistryParserSpecial<V4> {
    static final RegistrySet<V4> REG = new RegistryParserSpecialIPv4().load(IanaIpv4SpecialRegistry.bytes());

    private RegistryParserSpecialIPv4() {}

    @Override
    AddressSet<V4> addresses(Node record) throws XPathExpressionException {
        String a = address.evaluate(record).trim();
        if (a.contains(",")) {
            List<Block<V4>> addrs = Stream.of(a.split(", "))
                    .map(String::trim)
                    .map(s-> AddressSets.parseCidr(Family.v4(), s))
                    .collect(Collectors.toList());
            return AddressSets.from(addrs);
        }
        return AddressSets.parseCidr(Family.v4(), a);
    }
}
