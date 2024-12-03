package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class SetCache {
    private volatile AddressSet<?> v4;
    private volatile AddressSet<?> v6;

    @SuppressWarnings("unchecked")
    <A extends Address<A>> AddressSet<A> get(Function<Family<A>, RegistrySet<A>> source,
                                             Family<A> f,
                                             Predicate<Record<A>> matcher) {
        Objects.requireNonNull(f);
        if (f == Family.v4()) {
            if (v4 == null) {
                synchronized (this) {
                    if (v4 == null) {
                        v4 = lookup(source, f, matcher);
                    }
                }
            }
            return (AddressSet<A>) v4;
        }
        if (v6 == null) {
            synchronized (this) {
                if (v6 == null) {
                    v6 = lookup(source, f, matcher);
                }
            }
        }
        return (AddressSet<A>) v6;
    }

    private <A extends Address<A>> AddressSet<A> lookup(Function<Family<A>, RegistrySet<A>> source,
                                             Family<A> f,
                                             Predicate<Record<A>> matcher) {
        RegistrySet<A> reg = source.apply(f);
        List<AddressSet<A>> sets = reg.stream()
                .flatMap(RecordSet::stream)
                .filter(matcher)
                .map(Record::addresses)
                .collect(Collectors.toList());
        return AddressSets.from(sets);
    }
}
