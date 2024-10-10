package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Addressing {
    private Addressing() {}

    static <A extends Address<A>> AddressSet<A> parse(String id, Family<A> f, String address) {
        List<AddressSet<A>> sets = split(address)
                .map(s -> parse(f, s))
                .collect(Collectors.toList());
        return AddressSets.from(sets);
    }

    private static Stream<String> split(String a) {
        if (a.contains(",")) {
            return Stream.of(a.split(","))
                    .map(String::trim);
        }
        return Stream.of(a.trim());
    }

    private static <A extends Address<A>> AddressSet<A> parse(Family<A> f, String s) {
        return AddressSets.parseCidr(f, s);
    }

    static <A extends Address<A>> AddressSet<A> multi(String id, Family<A> f, String address) {
        if (address.contains("X")) {
            // IPv6 scoped addresses
            address = address.replace('X', '0');
        }
        if (address.contains("-")) {
            String[] addrs = address.split("-");
            A first = f.parse(addrs[0]);
            A last = f.parse(addrs[1]);
            return AddressSets.range(first, last);
        }
        if (address.contains("/")) {
            return AddressSets.parseCidr(f, address);
        }
        A addr = f.parse(address);
        return AddressSets.address(addr);
    }

    static <A extends Address<A>> AddressSet<A> rel(String id, Family<A> f, String address) {
        int radix = f == Family.v4()
                ? 10
                : 16;
        int start;
        int end;
        if (address.contains("-")) {
            String[] s = address.split("-");
            start = Integer.parseInt(s[0], radix);
            end = Integer.parseInt(s[1], radix);
        } else {
            start = Integer.parseInt(address, radix);
            end = start;
        }
        return AddressSets.range(f.parse(start), f.parse(end));
    }
}
