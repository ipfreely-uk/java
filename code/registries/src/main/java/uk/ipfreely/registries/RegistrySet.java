package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.List;

/**
 * Registry - container of {@link RecordSet} instances.
 *
 * @param <A> address type
 */
public final class RegistrySet<A extends Address<A>> extends Registry<A, RecordSet<A>> {
    RegistrySet(String title, String id, List<RecordSet<A>> contents) {
        super(title, id, contents);
    }
}
