package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.List;

/**
 * Registry - container of {@link Record} instances.
 *
 * @param <A> address type
 */
public final class RecordSet<A extends Address<A>> extends Registry<A, Record<A>> {
    RecordSet(String title, String id, List<Record<A>> contents) {
        super(title, id, contents);
    }
}
