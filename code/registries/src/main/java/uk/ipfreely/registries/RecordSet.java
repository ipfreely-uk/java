package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.List;

/**
 * Registry record collection.
 *
 * @param <A> address type
 */
public final class RecordSet<A extends Address<A>> extends Registry<Record<A>> {
    RecordSet(String title, String id, List<Record<A>> contents) {
        super(title, id, contents);
    }
}
