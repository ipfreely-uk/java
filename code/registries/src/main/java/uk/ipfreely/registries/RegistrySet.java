package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry - container of {@link RecordSet} instances.
 *
 * @param <A> address type
 */
public final class RegistrySet<A extends Address<A>> extends Registry<A, RecordSet<A>> {
    private volatile AddressSet<A> all;

    RegistrySet(String title, String id, List<RecordSet<A>> contents) {
        super(title, id, contents);
    }

    @Override
    AddressSet<A> union() {
        if (all == null) {
            collect();
        }
        return all;
    }

    private synchronized void collect() {
        if (all != null) {
            return;
        }
        List<AddressSet<A>> sets = stream()
                .flatMap(Registry::stream)
                .map(Record::addresses)
                .collect(Collectors.toList());
        all = AddressSets.from(sets);
    }
}
