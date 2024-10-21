package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;
import uk.ipfreely.sets.AddressSets;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry - container of {@link Record} instances.
 *
 * @param <A> address type
 */
public final class RecordSet<A extends Address<A>> extends Registry<A, Record<A>> {
    private volatile AddressSet<A> all;

    RecordSet(String title, String id, List<Record<A>> contents) {
        super(title, id, contents);
    }

    @Override
    public AddressSet<A> addresses() {
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
                .map(Record::addresses)
                .collect(Collectors.toList());
        all = AddressSets.from(sets);
    }
}
