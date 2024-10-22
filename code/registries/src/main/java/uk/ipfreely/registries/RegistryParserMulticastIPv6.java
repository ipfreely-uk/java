package uk.ipfreely.registries;

import org.w3c.dom.Node;
import uk.ipfreely.Family;
import uk.ipfreely.V6;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

final class RegistryParserMulticastIPv6 extends RegistryParserMulticast<V6> {
    static final RegistrySet<V6> REG;
    static final RecordSet<V6> SCOPES;
    static {
        RegistrySet<V6> all = new RegistryParserMulticastIPv6().load(Ipv6MulticastAddresses.bytes());
        List<RecordSet<V6>> ranges = new ArrayList<>();
        RecordSet<V6> scopes = null;
        for (RecordSet<V6> r : all) {
            if (r.id().equals("multicast-addresses-13")) {
                scopes = r;
            } else {
                ranges.add(r);
            }
        }
        SCOPES = scopes;
        REG = new RegistrySet<>(all.title(), all.id(), ranges);
    }

    private final XPathExpression value = exp("a:value");

    @Override
    AddressSet<V6> scope(Node record) throws XPathExpressionException {
        String n = value.evaluate(record).trim();
        if (n.contains("-")) {
            String[] range = n.split("-");
            V6 first = scoped(range[0]);
            V6 last = scoped(range[1]);
            return AddressSets.range(first, last);
        }
        V6 a = scoped(n);
        return AddressSets.address(a);
    }

    private V6 scoped(String n) {
        // TODO: fix
        int i = Integer.parseInt(n, 16);
        return Family.v6().parse(i);
    }

    @Override
    AddressSet<V6> parse(String address) {
        if (address.contains("X")) {
            return scopedRange(address);
        }
        if (address.contains("-")) {
            String[] range = address.split("-");
            V6 first = Family.v6().parse(range[0]);
            V6 last = Family.v6().parse(range[1]);
            return AddressSets.range(first, last);
        }
        if (address.contains("/")) {
            return AddressSets.parseCidr(Family.v6(), address);
        }
        V6 a = Family.v6().parse(address);
        return AddressSets.address(a);
    }

    private AddressSet<V6> scopedRange(String address) {
        List<AddressSet<V6>> all = new ArrayList<>(16);
        for (int i = 0; i <= 0xF; i++) {
            char digit = (char) (i < 0xA ? i + '0' : i - 0xA + 'A');
            String exd = address.replace('X', digit);
            AddressSet<V6> set = parse(exd);
            all.add(set);
        }
        return AddressSets.from(all);
    }
}
